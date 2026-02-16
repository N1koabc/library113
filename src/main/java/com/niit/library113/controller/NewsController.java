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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
@CrossOrigin
public class NewsController {

    @Autowired
    private NewsService newsService;
    @Autowired
    private UserService userService;

    // 注入新加的 Mapper，如果报错请确认 CommentMapper 文件已创建
    @Autowired(required = false)
    private CommentMapper commentMapper;

    // --- 原有接口保持不变 ---

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestNews() {
        List<News> list = newsService.list(new QueryWrapper<News>().orderByDesc("publish_date").last("LIMIT 6"));
        return ResponseEntity.ok(list);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNews(@RequestHeader(value = "user-id") Long userId, @RequestBody News news) {
        if (!userService.isAdmin(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权操作");

        news.setPublishDate(LocalDateTime.now());
        if(news.getCoverImage() == null || news.getCoverImage().isEmpty()) {
            news.setCoverImage("https://img.zcool.cn/community/01d90d5764ff650000012e7edb3052.jpg@1280w_1l_2o_100sh.jpg");
        }
        // 初始化计数器
        news.setLikeCount(0);
        news.setDislikeCount(0);
        return newsService.save(news) ? ResponseEntity.ok("发布成功") : ResponseEntity.badRequest().body("发布失败");
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteNews(@RequestHeader(value = "user-id") Long userId, @RequestBody News news) {
        if (!userService.isAdmin(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权操作");
        return newsService.removeById(news.getId()) ? ResponseEntity.ok("已删除") : ResponseEntity.badRequest().body("删除失败");
    }

    // --- 【新增功能】互动接口 ---

    // 1. 点赞/踩
    @PostMapping("/action")
    public ResponseEntity<?> action(@RequestBody Map<String, Object> params) {
        Long newsId = Long.valueOf(params.get("newsId").toString());
        String type = (String) params.get("type"); // "like" 或 "dislike"

        News news = newsService.getById(newsId);
        if (news != null) {
            if ("like".equals(type)) {
                news.setLikeCount((news.getLikeCount() == null ? 0 : news.getLikeCount()) + 1);
            } else if ("dislike".equals(type)) {
                news.setDislikeCount((news.getDislikeCount() == null ? 0 : news.getDislikeCount()) + 1);
            }
            newsService.updateById(news);
        }
        return ResponseEntity.ok("操作成功");
    }

    // 2. 获取评论列表
    @GetMapping("/comments")
    public ResponseEntity<?> getComments(@RequestParam Long newsId) {
        List<Comment> comments = commentMapper.selectList(
                new QueryWrapper<Comment>().eq("news_id", newsId).orderByDesc("create_time")
        );
        // 填充用户信息
        for (Comment c : comments) {
            User u = userService.getById(c.getUserId());
            if (u != null) {
                c.setUserName(u.getRealName());
                c.setUserAvatar(u.getAvatar());
            } else {
                c.setUserName("未知用户");
                c.setUserAvatar("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");
            }
        }
        return ResponseEntity.ok(comments);
    }

    // 3. 发表评论
    @PostMapping("/comment")
    public ResponseEntity<?> postComment(@RequestHeader("user-id") Long userId, @RequestBody Comment comment) {
        comment.setUserId(userId);
        comment.setCreateTime(LocalDateTime.now());
        commentMapper.insert(comment);
        return ResponseEntity.ok("评论成功");
    }
}