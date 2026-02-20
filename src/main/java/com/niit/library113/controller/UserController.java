package com.niit.library113.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niit.library113.dto.LoginDTO;
import com.niit.library113.entity.User;
import com.niit.library113.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random; // 【新增】引入随机数工具

@RestController
@RequestMapping("/api/user")
@CrossOrigin // 允许跨域
public class UserController {

    @Autowired
    private UserService userService;

    // ==========================================
    // 【新增】内置二次元/卡通头像库
    // 答辩防翻车设计：这些是超稳定的高颜值二次元矢量图 CDN 链接。
    // （如果您想用您自己收藏的真实动漫人物图片，只需提前把图片放到后端的 uploads/ 文件夹里，
    // 然后把这里的链接改成 "/images/1.jpg", "/images/2.png" 即可！）
    // ==========================================
    private static final String[] ANIME_AVATARS = {
            "https://api.dicebear.com/7.x/lorelei/svg?seed=Sasha&backgroundColor=b6e3f4", // 唯美二次元风 (蓝底)
            "https://api.dicebear.com/7.x/lorelei/svg?seed=Lily&backgroundColor=ffdfbf",  // 唯美二次元风 (橙底)
            "https://api.dicebear.com/7.x/lorelei/svg?seed=Jack&backgroundColor=c0aede",  // 唯美二次元风 (紫底)
            "https://api.dicebear.com/7.x/lorelei/svg?seed=Mia&backgroundColor=ffd5dc",   // 唯美二次元风 (粉底)
            "https://api.dicebear.com/7.x/adventurer/svg?seed=Abby&backgroundColor=d1d4f9", // 冒险家画风 (紫底)
            "https://api.dicebear.com/7.x/adventurer/svg?seed=Caleb&backgroundColor=b6e3f4",// 冒险家画风 (蓝底)
            "https://api.dicebear.com/7.x/adventurer/svg?seed=Destiny&backgroundColor=ffdfbf",// 冒险家画风 (橙底)
            "https://api.dicebear.com/7.x/notionists/svg?seed=Zoe&backgroundColor=c0aede"   // 极简可爱画风 (紫底)
    };

    // 1. 登录接口 (保持原有)
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDTO loginDTO) {
        try {
            User user = userService.login(loginDTO);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. 注册接口 (新增随机抽取二次元头像逻辑)
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User user) {
        try {
            // 【核心修改】：如果前端没有传头像，注册时随机抽取一个二次元头像分配给用户
            if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
                Random random = new Random();
                int randomIndex = random.nextInt(ANIME_AVATARS.length);
                user.setAvatar(ANIME_AVATARS[randomIndex]);
            }

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

    // 3. 获取信息 (保持原有)
    @GetMapping("/info")
    public ResponseEntity getUserInfo(@RequestParam Long userId) {
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // 4. 更新信息 (保持原有)
    @PostMapping("/update")
    public ResponseEntity updateUser(@RequestBody User user) {
        try {
            boolean success = userService.updateUserInfo(user);
            return success ? ResponseEntity.ok("更新成功") : ResponseEntity.badRequest().body("更新失败");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 5. 找回密码 (完美同步MD5加密逻辑)
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> params) {
        String username = params.get("username") != null ? params.get("username").trim() : null;
        String phone = params.get("phone");
        String verifyCode = params.get("verifyCode");
        String newPassword = params.get("newPassword");

        if (username == null || username.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty() ||
                verifyCode == null || verifyCode.trim().isEmpty() ||
                newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("请填写完整的核验信息与新密码");
        }

        if (!"123456".equals(verifyCode)) {
            return ResponseEntity.badRequest().body("验证码错误或已过期");
        }

        User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            return ResponseEntity.badRequest().body("该学号未注册");
        }

        String encryptedPwd = DigestUtils.md5DigestAsHex(newPassword.getBytes(StandardCharsets.UTF_8));
        user.setPassword(encryptedPwd);

        boolean success = userService.updateById(user);
        if (success) {
            return ResponseEntity.ok("密码重置成功，请使用新密码登录");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("系统繁忙，修改失败");
        }
    }
}