package com.niit.library113.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niit.library113.entity.Message;
import com.niit.library113.mapper.MessageMapper;
import com.niit.library113.service.MessageService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public void send(Long userId, String title, String content) {
        Message msg = new Message();
        msg.setUserId(userId);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setIsRead(false);
        msg.setCreateTime(LocalDateTime.now());
        this.save(msg);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return this.count(new QueryWrapper<Message>()
                .eq("user_id", userId)
                .eq("is_read", 0));
    }
}