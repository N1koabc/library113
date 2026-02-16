package com.niit.library113.controller;

import com.niit.library113.entity.Notice;
import com.niit.library113.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notices")
@CrossOrigin
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/latest")
    public ResponseEntity<?> getLatest() {
        try {
            List<Notice> list = noticeService.getLatestNotices();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("获取公告失败");
        }
    }
}