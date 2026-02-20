package com.niit.library113.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niit.library113.entity.Reservation;
import com.niit.library113.entity.Seat;
import com.niit.library113.mapper.ReservationMapper;
import com.niit.library113.mapper.SeatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reservation")
@CrossOrigin
public class ReservationController {

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private SeatMapper seatMapper;

    @GetMapping("/my-active")
    public ResponseEntity<?> getMyActiveReservation(@RequestParam Long userId) {
        Reservation res = reservationMapper.selectOne(
                new QueryWrapper<Reservation>().eq("user_id", userId).in("status", 0, 1).orderByDesc("id").last("LIMIT 1")
        );

        if (res == null) return ResponseEntity.ok().build();

        Seat seat = seatMapper.selectById(res.getSeatId());
        Map<String, Object> result = new HashMap<>();
        result.put("reservation", res);
        result.put("seatNumber", seat != null ? seat.getSeatNumber() : "未知");

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        long remainingSeconds = 0;
        String timeState = "";

        if (res.getStatus() == 0) {
            LocalDateTime deadline = res.getStartTime().plusMinutes(15);
            if (now.isBefore(res.getStartTime())) {
                remainingSeconds = Duration.between(now, res.getStartTime()).getSeconds();
                timeState = "NOT_STARTED";
            } else if (now.isBefore(deadline)) {
                remainingSeconds = Duration.between(now, deadline).getSeconds();
                timeState = "URGENT";
            } else {
                timeState = "OVERDUE";
            }
        } else {
            LocalDateTime deadline = res.getEndTime();
            if (now.isBefore(deadline)) {
                remainingSeconds = Duration.between(now, deadline).getSeconds();
                timeState = remainingSeconds <= 900 ? "URGENT" : "NORMAL";
            } else {
                timeState = "OVERDUE";
            }
        }

        result.put("remainingSeconds", remainingSeconds);
        result.put("timeState", timeState);
        return ResponseEntity.ok(result);
    }

    // 【核心修改】：接受前端传来的延长时间(分钟)
    @PostMapping("/extend")
    public ResponseEntity<?> extendReservation(@RequestParam Long reservationId, @RequestParam Integer extendMinutes) {
        // 校验续签时间是否在合法范围内（30~120分钟）
        if (extendMinutes == null || extendMinutes < 30 || extendMinutes > 120) {
            return ResponseEntity.badRequest().body("续签时间只能在30分钟到2小时之间");
        }

        Reservation res = reservationMapper.selectById(reservationId);
        if (res == null || res.getStatus() != 1) {
            return ResponseEntity.badRequest().body("只有正在使用中的座位才可以续签！");
        }

        LocalDateTime limitTime = LocalDateTime.of(LocalDate.now(ZoneId.of("Asia/Shanghai")), LocalTime.of(22, 0));
        if (!res.getEndTime().isBefore(limitTime)) {
            return ResponseEntity.badRequest().body("已达到今日闭馆时间(22:00)，无法继续续签。");
        }

        // 根据用户选择的分钟数进行累加
        LocalDateTime newEndTime = res.getEndTime().plusMinutes(extendMinutes);
        if (newEndTime.isAfter(limitTime)) {
            newEndTime = limitTime; // 封顶 22:00
        }

        res.setEndTime(newEndTime);
        reservationMapper.updateById(res);
        return ResponseEntity.ok("续签成功！使用时间已延长至 " + newEndTime.format(DateTimeFormatter.ofPattern("HH:mm")));
    }
}