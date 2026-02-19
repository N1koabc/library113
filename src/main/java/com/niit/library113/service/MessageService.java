package com.niit.library113.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niit.library113.entity.Message;

public interface MessageService extends IService<Message> {
    void send(Long userId, String title, String content);
    void send(Long userId, String title, String content, Long relatedId);

    // 【新增】支持传入评论ID的方法
    void send(Long userId, String title, String content, Long relatedId, Long commentId);

    long getUnreadCount(Long userId);
}