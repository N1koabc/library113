package com.niit.library113.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niit.library113.entity.Notice;
import com.niit.library113.mapper.NoticeMapper;
import com.niit.library113.service.NoticeService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public List<Notice> getLatestNotices() {
        // 按创建时间倒序，取前 5 条
        return this.list(new QueryWrapper<Notice>()
                .orderByDesc("create_time")
                .last("LIMIT 5"));
    }
}