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
        send(userId, title, content, null, null);
    }

    @Override
    public void send(Long userId, String title, String content, Long relatedId) {
        send(userId, title, content, relatedId, null);
    }

    // 【新增】保存带有 commentId 的消息
    @Override
    public void send(Long userId, String title, String content, Long relatedId, Long commentId) {
        Message msg = new Message();
        msg.setUserId(userId);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setRelatedId(relatedId);
        msg.setCommentId(commentId);
        msg.setCreateTime(LocalDateTime.now());
        msg.setIsRead(false);
        this.save(msg);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return this.count(new QueryWrapper<Message>().eq("user_id", userId).eq("is_read", false));
    }
}