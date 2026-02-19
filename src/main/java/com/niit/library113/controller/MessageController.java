package com.niit.library113.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niit.library113.entity.Message;
import com.niit.library113.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/mine")
    public ResponseEntity<?> getMyMessages(@RequestParam Long userId) {
        return ResponseEntity.ok(messageService.list(
                new QueryWrapper<Message>()
                        .eq("user_id", userId)
                        .orderByDesc("create_time")
                        .last("LIMIT 50")
        ));
    }

    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadCount(@RequestParam Long userId) {
        return ResponseEntity.ok(messageService.getUnreadCount(userId));
    }

    @PostMapping("/read/{id}")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        Message msg = messageService.getById(id);
        if (msg != null) {
            msg.setIsRead(true);
            messageService.updateById(msg);
        }
        return ResponseEntity.ok("已读");
    }

    // 【新增】一键全已读功能
    @PostMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@RequestParam Long userId) {
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId).eq("is_read", false).set("is_read", true);
        messageService.update(updateWrapper);
        return ResponseEntity.ok("全部已读");
    }
}