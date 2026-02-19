package com.niit.library113.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_action")
public class UserAction {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long targetId;
    private String targetType; // "NEWS" 或 "COMMENT"
    private String actionType; // "LIKE" 或 "DISLIKE"
    private LocalDateTime createTime;
}