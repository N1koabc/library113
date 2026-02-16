package com.niit.library113.controller;

import com.niit.library113.dto.LoginDTO;
import com.niit.library113.entity.User;
import com.niit.library113.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin // 允许跨域
public class UserController {

    @Autowired
    private UserService userService;

    // 1. 登录接口 (修复前缺失)
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDTO loginDTO) {
        try {
            User user = userService.login(loginDTO);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. 注册接口 (修复前缺失)
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User user) {
        try {
            boolean success = userService.register(user);
            if (success) {
                return ResponseEntity.ok("注册成功");
            } else {
                return ResponseEntity.badRequest().body("注册失败");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. 获取信息
    @GetMapping("/info")
    public ResponseEntity getUserInfo(@RequestParam Long userId) {
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // 4. 更新信息 (修复前使用的是 UserMapper，现改为 Service)
    @PostMapping("/update")
    public ResponseEntity updateUser(@RequestBody User user) {
        try {
            boolean success = userService.updateUserInfo(user);
            return success ? ResponseEntity.ok("更新成功") : ResponseEntity.badRequest().body("更新失败");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}