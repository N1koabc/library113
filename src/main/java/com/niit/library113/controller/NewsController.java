package com.niit.library113.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niit.library113.entity.Comment;
import com.niit.library113.entity.News;
import com.niit.library113.entity.User;
import com.niit.library113.mapper.CommentMapper;
import com.niit.library113.service.NewsService;
import com.niit.library113.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/news")
@CrossOrigin
public class NewsController {

    @Autowired private NewsService newsService;
    @Autowired private UserService userService;
    @Autowired(required = false) private CommentMapper commentMapper;

    // --- 原有基础接口 ---
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestNews() {
        return ResponseEntity.ok(newsService.list(new QueryWrapper<News>().orderByDesc("publish_date").last("LIMIT 10")));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNews(@RequestHeader("user-id") Long userId, @RequestBody News news) {
        if (!userService.isAdmin(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        news.setPublishDate(LocalDateTime.now());
        if(news.getCoverImage() == null || news.getCoverImage().isEmpty()) news.setCoverImage("https://img.zcool.cn/community/01d90d5764ff650000012e7edb3052.jpg@1280w_1l_2o_100sh.jpg");
        news.setLikeCount(0); news.setDislikeCount(0);
        return newsService.save(news) ? ResponseEntity.ok("发布成功") : ResponseEntity.badRequest().body("失败");
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteNews(@RequestHeader("user-id") Long userId, @RequestBody News news) {
        if (!userService.isAdmin(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return newsService.removeById(news.getId()) ? ResponseEntity.ok("删除成功") : ResponseEntity.badRequest().body("失败");
    }

    // --- 新闻互动 ---
    @PostMapping("/action")
    public ResponseEntity<?> action(@RequestBody Map<String, Object> params) {
        Long newsId = Long.valueOf(params.get("newsId").toString());
        String type = (String) params.get("type");
        News news = newsService.getById(newsId);
        if (news != null) {
            if ("like".equals(type)) news.setLikeCount(news.getLikeCount() + 1);
            else if ("dislike".equals(type)) news.setDislikeCount(news.getDislikeCount() + 1);
            newsService.updateById(news);
        }
        return ResponseEntity.ok("操作成功");
    }

    // --- 【升级】获取评论（支持楼中楼） ---
    @GetMapping("/comments")
    public ResponseEntity<?> getComments(@RequestParam Long newsId) {
        // 1. 查出所有评论
        List<Comment> allComments = commentMapper.selectList(new QueryWrapper<Comment>().eq("news_id", newsId).orderByDesc("create_time"));

        // 2. 填充用户信息
        for (Comment c : allComments) {
            User u = userService.getById(c.getUserId());
            c.setUserName(u != null ? u.getRealName() : "未知用户");
            c.setUserAvatar(u != null ? u.getAvatar() : "");
            c.setLikeCount(c.getLikeCount() == null ? 0 : c.getLikeCount());
            c.setDislikeCount(c.getDislikeCount() == null ? 0 : c.getDislikeCount());
        }

        // 3. 组装父子结构
        List<Comment> rootComments = allComments.stream().filter(c -> c.getParentId() == null).collect(Collectors.toList());
        for (Comment root : rootComments) {
            List<Comment> replies = allComments.stream()
                    .filter(c -> c.getParentId() != null && c.getParentId().equals(root.getId()))
                    .sorted((a, b) -> a.getCreateTime().compareTo(b.getCreateTime())) // 回复按时间正序
                    .collect(Collectors.toList());
            root.setReplies(replies);
        }

        return ResponseEntity.ok(rootComments);
    }

    // --- 【升级】发表评论/回复 ---
    @PostMapping("/comment")
    public ResponseEntity<?> postComment(@RequestHeader("user-id") Long userId, @RequestBody Comment comment) {
        comment.setUserId(userId);
        comment.setCreateTime(LocalDateTime.now());
        comment.setLikeCount(0);
        comment.setDislikeCount(0);
        commentMapper.insert(comment);
        return ResponseEntity.ok("评论成功");
    }

    // --- 【新增】评论点赞/踩 ---
    @PostMapping("/comment/action")
    public ResponseEntity<?> commentAction(@RequestBody Map<String, Object> params) {
        Long commentId = Long.valueOf(params.get("commentId").toString());
        String type = (String) params.get("type"); // "like" or "dislike"

        Comment c = commentMapper.selectById(commentId);
        if (c != null) {
            if ("like".equals(type)) c.setLikeCount((c.getLikeCount() == null ? 0 : c.getLikeCount()) + 1);
            else if ("dislike".equals(type)) c.setDislikeCount((c.getDislikeCount() == null ? 0 : c.getDislikeCount()) + 1);
            commentMapper.updateById(c);
        }
        return ResponseEntity.ok("操作成功");
    }
}