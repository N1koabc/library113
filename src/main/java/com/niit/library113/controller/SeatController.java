package com.niit.library113.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niit.library113.dto.ReservationRequest;
import com.niit.library113.entity.Reservation;
import com.niit.library113.entity.Seat;
import com.niit.library113.mapper.ReservationMapper;
import com.niit.library113.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/seats")
@CrossOrigin
public class SeatController {

    @Autowired
    private SeatService seatService;
    @Autowired
    private ReservationMapper reservationMapper;

    @GetMapping("/list")
    public ResponseEntity<?> getSeats(@RequestParam Integer floor, @RequestParam String zone,
                                      @RequestParam(required = false) String checkStartTime, @RequestParam(required = false) String checkEndTime) {
        try {
            LocalDateTime start = (checkStartTime != null && !checkStartTime.isEmpty() && !"null".equals(checkStartTime)) ? LocalDateTime.parse(checkStartTime) : null;
            LocalDateTime end = (checkEndTime != null && !checkEndTime.isEmpty() && !"null".equals(checkEndTime)) ? LocalDateTime.parse(checkEndTime) : null;
            return ResponseEntity.ok(seatService.findAvailableSeats(floor, zone, start, end));
        } catch (Exception e) { return ResponseEntity.badRequest().body("时间格式错误"); }
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookSeat(@RequestBody ReservationRequest request) {
        try {
            seatService.createReservation(request);
            return ResponseEntity.ok("预约成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelSeat(@RequestBody ReservationRequest request) {
        try {
            seatService.cancelReservation(request);
            return ResponseEntity.ok("退座成功");
        } catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @GetMapping("/mine")
    public ResponseEntity<?> getMySeat(@RequestParam Long userId) {
        Reservation res = reservationMapper.selectOne(new QueryWrapper<Reservation>().eq("user_id", userId).in("status", 0, 1).last("LIMIT 1"));
        if (res != null) {
            Seat seat = seatService.getById(res.getSeatId());
            Map<String, Object> result = new HashMap<>();
            result.put("id", res.getId());
            result.put("seatId", seat.getId());
            result.put("seatNumber", seat.getSeatNumber());
            result.put("status", res.getStatus());
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping("/checkin")
    public ResponseEntity<?> checkIn(@RequestBody Map<String, Long> params) {
        try { seatService.checkIn(params.get("id")); return ResponseEntity.ok("签到成功"); } catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkOut(@RequestBody Map<String, Long> params) {
        try { seatService.checkOut(params.get("id")); return ResponseEntity.ok("签退成功，信用分+1"); } catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    // 【新增】查询闭馆状态
    @GetMapping("/status")
    public ResponseEntity<?> getLibraryStatus() {
        Map<String, Boolean> map = new HashMap<>();
        map.put("closed", seatService.isLibraryClosed());
        return ResponseEntity.ok(map);
    }

    @GetMapping("/stats") public ResponseEntity<?> getStats() { return ResponseEntity.ok(seatService.getHeatmapData()); }
    @GetMapping("/saturation") public ResponseEntity<?> getSaturation() { return ResponseEntity.ok(seatService.getFloorSaturation()); }
    @GetMapping("/logs") public ResponseEntity<?> getLogs() { return ResponseEntity.ok(seatService.getLatestLogs()); }
    @GetMapping("/occupancy") public ResponseEntity<?> getOccupancy() { return ResponseEntity.ok(seatService.getLibraryOccupancy()); }
}