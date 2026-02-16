package com.niit.library113.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niit.library113.entity.Notice;
import java.util.List;

public interface NoticeService extends IService<Notice> {
    // 获取最新公告
    List<Notice> getLatestNotices();
}