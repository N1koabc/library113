package com.niit.library113.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niit.library113.dto.LoginDTO;
import com.niit.library113.entity.User;

public interface UserService extends IService<User> {
    // 登录逻辑
    User login(LoginDTO loginDTO);

    // 注册逻辑
    boolean register(User user);

    // 更新信息
    boolean updateUserInfo(User user);

    // 【新增】鉴权方法：判断是否为管理员
    boolean isAdmin(Long userId);
}