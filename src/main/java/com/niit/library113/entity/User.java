package com.niit.library113.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;    // 学号
    private String password;    // 密码
    private String realName;    // 真实姓名

    // 【新增】所属学院
    private String college;

    // 专业班级
    private String major;

    private Integer creditScore;// 信用分
    private String avatar;      // 头像链接

    // 上次登录时间
    private LocalDateTime lastLoginTime;

    private LocalDateTime createTime;

    // 身份标识
    @TableField(exist = false)
    private String role;
}