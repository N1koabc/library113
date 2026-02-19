package com.niit.library113.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niit.library113.entity.CreditLog;
import com.niit.library113.mapper.CreditLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credit")
@CrossOrigin
public class CreditController {

    @Autowired
    private CreditLogMapper creditLogMapper;

    @GetMapping("/logs")
    public ResponseEntity<?> getLogs(@RequestParam Long userId) {
        return ResponseEntity.ok(creditLogMapper.selectList(
                new QueryWrapper<CreditLog>().eq("user_id", userId).orderByDesc("create_time")
        ));
    }
}