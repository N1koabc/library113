package com.niit.library113.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("reservation")
public class Reservation {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long seatId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    /**
     * 状态: 0=预约中, 1=使用中, 2=已结束, 4=已取消
     */
    private Integer status;

    /**
     * 创建时间 (之前报错是因为缺少这个字段)
     */
    private LocalDateTime createTime;
}