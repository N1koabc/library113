package com.niit.library113.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niit.library113.entity.Message;
import com.niit.library113.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/mine")
    public ResponseEntity<?> getMyMessages(@RequestParam Long userId) {
        List<Message> list = messageService.list(new QueryWrapper<Message>()
                .eq("user_id", userId)
                .orderByDesc("create_time"));
        return ResponseEntity.ok(list);
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
        return ResponseEntity.ok("ok");
    }
}