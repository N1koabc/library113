package com.niit.library113.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niit.library113.entity.Message;

public interface MessageService extends IService<Message> {
    // 发送消息给用户
    void send(Long userId, String title, String content);

    // 获取用户未读数量
    long getUnreadCount(Long userId);
}