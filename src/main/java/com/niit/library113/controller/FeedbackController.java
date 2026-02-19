package com.niit.library113.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niit.library113.entity.Feedback;
import com.niit.library113.entity.User;
import com.niit.library113.mapper.FeedbackMapper;
import com.niit.library113.service.MessageService;
import com.niit.library113.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin
public class FeedbackController {

    @Autowired private FeedbackMapper feedbackMapper;
    @Autowired private UserService userService;
    @Autowired private MessageService messageService;

    // 1. 用户提交反馈
    @PostMapping("/submit")
    public ResponseEntity<?> submitFeedback(@RequestHeader("user-id") Long userId, @RequestBody Feedback feedback) {
        feedback.setUserId(userId);
        feedback.setCreateTime(LocalDateTime.now());
        feedback.setStatus(0);
        feedbackMapper.insert(feedback);
        return ResponseEntity.ok("反馈提交成功");
    }

    // 2. 获取我的反馈
    @GetMapping("/mine")
    public ResponseEntity<?> getMyFeedbacks(@RequestHeader("user-id") Long userId) {
        return ResponseEntity.ok(feedbackMapper.selectList(
                new QueryWrapper<Feedback>().eq("user_id", userId).orderByDesc("create_time")
        ));
    }

    // 3. 管理员获取所有反馈
    @GetMapping("/all")
    public ResponseEntity<?> getAllFeedbacks(@RequestHeader("user-id") Long userId) {
        if (!userService.isAdmin(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        List<Feedback> list = feedbackMapper.selectList(new QueryWrapper<Feedback>().orderByAsc("status").orderByDesc("create_time"));
        for (Feedback f : list) {
            User u = userService.getById(f.getUserId());
            if (u != null) {
                f.setUserName(u.getRealName());
                f.setUserAvatar(u.getAvatar());
            }
        }
        return ResponseEntity.ok(list);
    }

    // 4. 管理员回复反馈
    @PostMapping("/reply")
    public ResponseEntity<?> replyFeedback(@RequestHeader("user-id") Long userId, @RequestBody Feedback req) {
        if (!userService.isAdmin(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        Feedback f = feedbackMapper.selectById(req.getId());
        if (f != null) {
            f.setStatus(1);
            f.setReply(req.getReply());
            feedbackMapper.updateById(f);

            // 联动消息系统发通知
            messageService.send(f.getUserId(), "您的反馈有了新进展", "管理员回复：" + req.getReply());
        }
        return ResponseEntity.ok("回复成功");
    }
}