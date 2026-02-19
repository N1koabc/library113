package com.niit.library113.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("feedback")
public class Feedback {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String type;
    private String content;
    private Integer status = 0;
    private String reply;
    private LocalDateTime createTime;

    // 辅助字段（用于管理员界面显示发件人）
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String userAvatar;
}