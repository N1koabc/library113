package com.niit.library113.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niit.library113.dto.LoginDTO;
import com.niit.library113.entity.User;
import com.niit.library113.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    // ==========================================
    // 【新增】忘记密码 - 身份核验与重置
    // ==========================================
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String realName = params.get("realName");
        String newPassword = params.get("newPassword");

        if (username == null || username.trim().isEmpty() ||
                realName == null || realName.trim().isEmpty() ||
                newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("请填写完整的核验信息与新密码");
        }

        // 1. 查找用户是否存在
        User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            return ResponseEntity.badRequest().body("该学号未注册");
        }

        // 2. 核心：核验真实姓名是否匹配
        if (!user.getRealName().equals(realName)) {
            return ResponseEntity.badRequest().body("学号与绑定的真实姓名不匹配，无法重置");
        }

        // 3. 更新密码
        // ⚠️【注意】：您提到密码不是明文。如果您的加密逻辑是写在 Controller 里的，
        // 请在这里把 newPassword 换成您的加密代码（如 MD5 或 BCrypt）。
        // 如果您的加密是写在拦截器或服务层的，直接 set 即可。
        user.setPassword(newPassword);

        userService.updateById(user);
        return ResponseEntity.ok("密码重置成功，请使用新密码登录");
    }
}