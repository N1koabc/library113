package com.niit.library113.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long newsId;
    private Long userId;
    private Long parentId; // 根评论ID (楼层ID)
    private Long replyToUserId; // 【新增】具体回复了谁 (用于发通知)
    private String content;
    private Integer likeCount = 0;
    private Integer dislikeCount = 0;
    private LocalDateTime createTime;

    // --- 辅助字段 (前端显示用) ---
    @TableField(exist = false)
    private String userName;     // 发评人昵称
    @TableField(exist = false)
    private String userAvatar;   // 发评人头像
    @TableField(exist = false)
    private String replyToUserName; // 【新增】被回复人的昵称 (显示 @张三)
    @TableField(exist = false)
    private List<Comment> replies; // 子评论列表
}