package com.niit.library113.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long newsId;
    private Long userId;
    private String content;
    private LocalDateTime createTime;

    // 辅助字段：用于给前端显示用户头像和名字
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String userAvatar;
}