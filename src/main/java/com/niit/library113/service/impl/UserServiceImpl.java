package com.niit.library113.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niit.library113.dto.LoginDTO;
import com.niit.library113.entity.User;
import com.niit.library113.mapper.UserMapper;
import com.niit.library113.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User login(LoginDTO loginDTO) {
        if (!StringUtils.hasText(loginDTO.getUsername()) || !StringUtils.hasText(loginDTO.getPassword())) {
            throw new RuntimeException("账号或密码不能为空");
        }

        String encryptedPwd = DigestUtils.md5DigestAsHex(loginDTO.getPassword().getBytes(StandardCharsets.UTF_8));

        User user = this.getOne(new QueryWrapper<User>()
                .eq("username", loginDTO.getUsername())
                .eq("password", encryptedPwd));

        if (user == null) {
            throw new RuntimeException("账号或密码错误");
        }

        // 更新登录时间
        user.setLastLoginTime(LocalDateTime.now());
        this.updateById(user);

        // 设置前端所需的角色标识 (不存库，仅运行时计算)
        if ("admin".equals(user.getUsername())) {
            user.setRole("admin");
        } else {
            user.setRole("user");
        }

        user.setPassword(null); // 隐藏密码
        return user;
    }

    @Override
    public boolean register(User user) {
        if (!StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            throw new RuntimeException("信息不完整");
        }

        long count = this.count(new QueryWrapper<User>().eq("username", user.getUsername()));
        if (count > 0) {
            throw new RuntimeException("该账号已被注册");
        }

        String encryptedPwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8));
        user.setPassword(encryptedPwd);

        if (user.getCreditScore() == null) user.setCreditScore(100);
        if (!StringUtils.hasText(user.getAvatar())) {
            user.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=" + user.getUsername());
        }
        user.setCreateTime(LocalDateTime.now());

        return this.save(user);
    }

    @Override
    public boolean updateUserInfo(User user) {
        User old = this.getById(user.getId());
        if(old == null) throw new RuntimeException("用户不存在");

        if(StringUtils.hasText(user.getRealName())) old.setRealName(user.getRealName());
        if(StringUtils.hasText(user.getAvatar())) old.setAvatar(user.getAvatar());
        if(StringUtils.hasText(user.getCollege())) old.setCollege(user.getCollege());
        if(StringUtils.hasText(user.getMajor())) old.setMajor(user.getMajor());

        // 【新增】更新手机号码
        if(StringUtils.hasText(user.getPhone())) old.setPhone(user.getPhone());

        return this.updateById(old);
    }

    // 后端核心鉴权逻辑
    @Override
    public boolean isAdmin(Long userId) {
        if (userId == null) return false;
        User user = this.getById(userId);
        // 这里定义谁是管理员：用户名为 admin 的用户
        return user != null && "admin".equals(user.getUsername());
    }
}