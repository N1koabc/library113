package com.niit.library113.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niit.library113.entity.Comment;
import com.niit.library113.entity.News;
import com.niit.library113.entity.User;
import com.niit.library113.entity.UserAction;
import com.niit.library113.mapper.CommentMapper;
import com.niit.library113.mapper.UserActionMapper;
import com.niit.library113.service.MessageService;
import com.niit.library113.service.NewsService;
import com.niit.library113.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/news")
@CrossOrigin
public class NewsController {

    @Autowired private NewsService newsService;
    @Autowired private UserService userService;
    @Autowired private MessageService messageService;
    @Autowired(required = false) private CommentMapper commentMapper;
    @Autowired(required = false) private UserActionMapper userActionMapper;

    @GetMapping("/detail")
    public ResponseEntity<?> getNewsDetail(@RequestParam Long id) {
        News news = newsService.getById(id);
        return news != null ? ResponseEntity.ok(news) : ResponseEntity.notFound().build();
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestNews() {
        return ResponseEntity.ok(newsService.list(new QueryWrapper<News>().orderByDesc("publish_date").last("LIMIT 10")));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNews(@RequestHeader("user-id") Long userId, @RequestBody News news) {
        if (!userService.isAdmin(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        news.setPublishDate(LocalDateTime.now());
        if(news.getCoverImage() == null || news.getCoverImage().isEmpty()) {
            news.setCoverImage("https://dummyimage.com/600x400/059669/ffffff&text=GUET+News");
        }
        news.setLikeCount(0); news.setDislikeCount(0);
        return newsService.save(news) ? ResponseEntity.ok("发布成功") : ResponseEntity.badRequest().body("失败");
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteNews(@RequestHeader("user-id") Long userId, @RequestBody News news) {
        if (!userService.isAdmin(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return newsService.removeById(news.getId()) ? ResponseEntity.ok("删除成功") : ResponseEntity.badRequest().body("失败");
    }

    @PostMapping("/action")
    public ResponseEntity<?> action(@RequestHeader("user-id") Long userId, @RequestBody Map<String, Object> params) {
        Long newsId = Long.valueOf(params.get("newsId").toString());
        String type = (String) params.get("type");
        Long count = userActionMapper.selectCount(new QueryWrapper<UserAction>().eq("user_id", userId).eq("target_id", newsId).eq("target_type", "NEWS"));
        if (count > 0) return ResponseEntity.badRequest().body("您已评价过");
        News news = newsService.getById(newsId);
        if (news != null) {
            if ("like".equals(type)) news.setLikeCount(news.getLikeCount() + 1); else news.setDislikeCount(news.getDislikeCount() + 1);
            newsService.updateById(news);
            UserAction action = new UserAction(); action.setUserId(userId); action.setTargetId(newsId); action.setTargetType("NEWS"); action.setActionType(type.toUpperCase()); action.setCreateTime(LocalDateTime.now());
            userActionMapper.insert(action);
        }
        return ResponseEntity.ok("操作成功");
    }

    @GetMapping("/comments")
    public ResponseEntity<?> getComments(@RequestParam Long newsId) {
        List<Comment> all = commentMapper.selectList(new QueryWrapper<Comment>().eq("news_id", newsId).orderByDesc("create_time"));
        for (Comment c : all) {
            User u = userService.getById(c.getUserId());
            c.setUserName(u != null ? u.getRealName() : "用户" + c.getUserId());
            c.setUserAvatar(u != null ? u.getAvatar() : "");
            if (c.getReplyToUserId() != null) {
                User target = userService.getById(c.getReplyToUserId());
                if(target != null) c.setReplyToUserName(target.getRealName());
            }
            if(c.getLikeCount() == null) c.setLikeCount(0); if(c.getDislikeCount() == null) c.setDislikeCount(0);
        }
        List<Comment> roots = all.stream().filter(c -> c.getParentId() == null).collect(Collectors.toList());
        for (Comment root : roots) {
            List<Comment> replies = all.stream().filter(c -> c.getParentId() != null && c.getParentId().equals(root.getId())).sorted((a, b) -> a.getCreateTime().compareTo(b.getCreateTime())).collect(Collectors.toList());
            root.setReplies(replies);
        }
        return ResponseEntity.ok(roots);
    }

    // === 核心逻辑修改区 ===
    @PostMapping("/comment")
    public ResponseEntity<?> postComment(@RequestHeader("user-id") Long userId, @RequestBody Comment comment) {
        comment.setUserId(userId);
        comment.setCreateTime(LocalDateTime.now());
        comment.setLikeCount(0); comment.setDislikeCount(0);

        Comment target = null;
        if (comment.getParentId() != null) {
            target = commentMapper.selectById(comment.getParentId());
            if (target != null) {
                if (target.getParentId() != null) comment.setParentId(target.getParentId());
                else comment.setParentId(target.getId());
                comment.setReplyToUserId(target.getUserId());
            }
        }

        // 1. 必须先插入数据库，产生自增的 ID
        commentMapper.insert(comment);

        // 2. 插入成功后，再发带有 comment.getId() 的通知
        if (target != null && !target.getUserId().equals(userId)) {
            User sender = userService.getById(userId);
            String name = sender != null ? sender.getRealName() : "有人";
            messageService.send(
                    target.getUserId(),
                    "新回复通知",
                    name + " 在评论区回复了你：" + comment.getContent(),
                    comment.getNewsId(),
                    comment.getId() // 这里传入刚刚生成的新评论ID
            );
        }
        return ResponseEntity.ok("评论成功");
    }

    @PostMapping("/comment/action")
    public ResponseEntity<?> commentAction(@RequestHeader("user-id") Long userId, @RequestBody Map<String, Object> params) {
        Long commentId = Long.valueOf(params.get("commentId").toString());
        String type = (String) params.get("type");
        Long count = userActionMapper.selectCount(new QueryWrapper<UserAction>().eq("user_id", userId).eq("target_id", commentId).eq("target_type", "COMMENT"));
        if (count > 0) return ResponseEntity.badRequest().body("您已评价过");
        Comment c = commentMapper.selectById(commentId);
        if (c != null) {
            if ("like".equals(type)) c.setLikeCount((c.getLikeCount()==null?0:c.getLikeCount()) + 1); else c.setDislikeCount((c.getDislikeCount()==null?0:c.getDislikeCount()) + 1);
            commentMapper.updateById(c);
            UserAction action = new UserAction(); action.setUserId(userId); action.setTargetId(commentId); action.setTargetType("COMMENT"); action.setActionType(type.toUpperCase()); action.setCreateTime(LocalDateTime.now());
            userActionMapper.insert(action);
        }
        return ResponseEntity.ok("已评价");
    }
}