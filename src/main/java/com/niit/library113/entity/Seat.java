package com.niit.library113.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("seat")
public class Seat {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String seatNumber;
    private Integer floor;
    private String zone;

    // 0:空闲 1:使用中 2:维护中
    private Integer status;

    // 【核心确认】：确保有这两个布尔字段
    private Boolean hasSocket;
    private Boolean isWindow;
}