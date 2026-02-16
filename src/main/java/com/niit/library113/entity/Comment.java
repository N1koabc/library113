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
    private String content;
    private LocalDateTime createTime;

    // 【新增】互动与结构字段
    private Integer likeCount = 0;
    private Integer dislikeCount = 0;
    private Long parentId; // 如果是回复，则存父评论ID

    // --- 辅助字段 (不存数据库) ---
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String userAvatar;

    // 用于存放子评论（回复）
    @TableField(exist = false)
    private List<Comment> replies;
}