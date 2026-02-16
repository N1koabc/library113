package com.niit.library113.dto;

import lombok.Data;

/**
 * 专门用于接收前端登录请求的参数
 * 包含学号和密码
 */
@Data
public class LoginDTO {
    private String username;
    private String password;
}