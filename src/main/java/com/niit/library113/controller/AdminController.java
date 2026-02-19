package com.niit.library113.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niit.library113.entity.*;
import com.niit.library113.mapper.CreditLogMapper;
import com.niit.library113.mapper.ReservationMapper;
import com.niit.library113.service.NoticeService;
import com.niit.library113.service.SeatService;
import com.niit.library113.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private SeatService seatService;
    @Autowired private NoticeService noticeService;
    @Autowired private ReservationMapper reservationMapper;
    @Autowired private CreditLogMapper creditLogMapper; // 新增注入

    private boolean checkPermission(Long userId) {
        return userService.isAdmin(userId);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@RequestHeader(value = "user-id", required = false) Long userId) {
        if (!checkPermission(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userService.count());
        data.put("currentOccupied", reservationMapper.selectCount(new QueryWrapper<Reservation>().eq("status", 1)));
        data.put("todayCount", reservationMapper.selectCount(new QueryWrapper<Reservation>().ge("create_time", LocalDate.now().atStartOfDay())));
        data.put("violations", reservationMapper.selectCount(new QueryWrapper<Reservation>().eq("status", 2)));
        return ResponseEntity.ok(data);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestHeader(value = "user-id", required = false) Long userId, @RequestParam(required = false) String keyword) {
        if (!checkPermission(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");
        QueryWrapper<User> query = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) { query.and(w -> w.like("username", keyword).or().like("real_name", keyword)); }
        query.orderByDesc("create_time");
        return ResponseEntity.ok(userService.list(query));
    }

    @PostMapping("/credit")
    public ResponseEntity<?> adjustCredit(@RequestHeader(value = "user-id", required = false) Long userId, @RequestBody Map<String, Object> params) {
        if (!checkPermission(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");
        String username = (String) params.get("username");
        Integer delta = (Integer) params.get("delta");

        User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) return ResponseEntity.badRequest().body("用户不存在");

        int oldScore = user.getCreditScore();
        int newScore = Math.max(0, Math.min(100, oldScore + delta));
        int actualDelta = newScore - oldScore; // 计算真实改变的值

        user.setCreditScore(newScore);
        userService.updateById(user);

        // 记录流水
        if (actualDelta != 0) {
            CreditLog log = new CreditLog();
            log.setUserId(user.getId());
            log.setDelta(actualDelta);
            log.setReason("管理员后台调整");
            log.setCreateTime(LocalDateTime.now());
            creditLogMapper.insert(log);
        }

        return ResponseEntity.ok("信用分已更新");
    }

    @PostMapping("/notice")
    public ResponseEntity<?> publishNotice(@RequestHeader(value = "user-id", required = false) Long userId, @RequestBody Notice notice) {
        if (!checkPermission(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");
        notice.setCreateTime(LocalDateTime.now());
        if (notice.getType() == null) notice.setType("info");
        return noticeService.save(notice) ? ResponseEntity.ok("发布成功") : ResponseEntity.badRequest().body("失败");
    }

    @PostMapping("/notice/delete")
    public ResponseEntity<?> deleteNotice(@RequestHeader(value = "user-id", required = false) Long userId, @RequestBody Map<String, Long> params) {
        if (!checkPermission(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");
        return noticeService.removeById(params.get("id")) ? ResponseEntity.ok("已撤回") : ResponseEntity.badRequest().body("操作失败");
    }

    @PostMapping("/seat/add")
    public ResponseEntity<?> addSeat(@RequestHeader(value = "user-id") Long userId, @RequestBody Seat seat) {
        if (!checkPermission(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权操作");
        Long count = seatService.count(new QueryWrapper<Seat>().eq("seat_number", seat.getSeatNumber()));
        if (count > 0) return ResponseEntity.badRequest().body("座位号 " + seat.getSeatNumber() + " 已存在");
        seat.setStatus(0);
        return seatService.save(seat) ? ResponseEntity.ok("座位添加成功") : ResponseEntity.badRequest().body("添加失败");
    }

    @PostMapping("/seat/delete")
    public ResponseEntity<?> deleteSeat(@RequestHeader(value = "user-id") Long userId, @RequestBody Map<String, Long> params) {
        if (!checkPermission(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权操作");
        Long seatId = params.get("id");
        Seat seat = seatService.getById(seatId);
        if (seat != null && seat.getStatus() == 1) return ResponseEntity.badRequest().body("该座位使用中，无法删除");
        return seatService.removeById(seatId) ? ResponseEntity.ok("已移除座位") : ResponseEntity.badRequest().body("删除失败");
    }

    @PostMapping("/seat/batch-delete")
    public ResponseEntity<?> batchDelete(@RequestHeader(value = "user-id") Long userId, @RequestBody Map<String, Object> params) {
        if (!checkPermission(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权操作");

        Integer floor = (Integer) params.get("floor");
        String zone = (String) params.get("zone");

        if (floor == null && (zone == null || "全部".equals(zone) || zone.isEmpty())) {
            return ResponseEntity.badRequest().body("必须指定楼层或区域");
        }

        QueryWrapper<Seat> checkQuery = new QueryWrapper<>();
        if (floor != null) checkQuery.eq("floor", floor);
        if (zone != null && !"全部".equals(zone) && !zone.isEmpty()) checkQuery.eq("zone", zone);
        checkQuery.eq("status", 1);
        if (seatService.count(checkQuery) > 0) {
            return ResponseEntity.badRequest().body("区域内有座位被使用，无法批量删除");
        }

        QueryWrapper<Seat> deleteQuery = new QueryWrapper<>();
        if (floor != null) deleteQuery.eq("floor", floor);
        if (zone != null && !"全部".equals(zone) && !zone.isEmpty()) deleteQuery.eq("zone", zone);

        return seatService.remove(deleteQuery) ? ResponseEntity.ok("清空成功") : ResponseEntity.badRequest().body("删除失败");
    }

    @PostMapping("/seat/maintenance")
    public ResponseEntity<?> toggleMaintenance(@RequestHeader(value = "user-id", required = false) Long userId, @RequestBody Map<String, Object> params) {
        if (!checkPermission(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");
        try {
            seatService.setSeatMaintenance(Long.valueOf(params.get("seatId").toString()), (Boolean) params.get("maintenance"));
            return ResponseEntity.ok("操作成功");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/seat/batch-maintenance")
    public ResponseEntity<?> batchMaintenance(@RequestHeader(value = "user-id") Long userId, @RequestBody Map<String, Object> params) {
        if (!checkPermission(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");
        Integer floor = params.get("floor") != null ? Integer.valueOf(params.get("floor").toString()) : null;
        String zone = (String) params.get("zone");
        Boolean maintenance = (Boolean) params.get("maintenance");
        try {
            seatService.batchMaintenance(floor, zone, maintenance);
            return ResponseEntity.ok("批量操作完成");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("失败：" + e.getMessage());
        }
    }

    @PostMapping("/library/toggle")
    public ResponseEntity<?> toggleLibrary(@RequestHeader(value = "user-id") Long userId, @RequestBody Map<String, Boolean> params) {
        if (!checkPermission(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");
        boolean close = params.get("close");
        if (close) {
            seatService.closeLibrary();
            return ResponseEntity.ok("已闭馆");
        } else {
            seatService.openLibrary();
            return ResponseEntity.ok("已恢复开馆");
        }
    }

    @GetMapping("/trend")
    public ResponseEntity<?> getTrendData(@RequestHeader(value = "user-id") Long userId, @RequestParam String type) {
        if (!checkPermission(userId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");

        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();
        String dateFormat;
        int steps;

        if ("day".equals(type)) {
            start = LocalDate.now().atStartOfDay(); end = LocalDate.now().atTime(LocalTime.MAX);
            dateFormat = "%H"; steps = 24;
        } else if ("month".equals(type)) {
            start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            dateFormat = "%d"; steps = LocalDate.now().lengthOfMonth();
        } else {
            start = LocalDate.now().withDayOfYear(1).atStartOfDay();
            dateFormat = "%m"; steps = 12;
        }

        QueryWrapper<Reservation> query = new QueryWrapper<>();
        query.select("DATE_FORMAT(create_time, '" + dateFormat + "') as key_str", "count(*) as total", "sum(case when status = 2 then 1 else 0 end) as vio")
                .ge("create_time", start).le("create_time", end).groupBy("key_str").orderByAsc("key_str");

        List<Map<String, Object>> dbList = reservationMapper.selectMaps(query);
        Map<String, Map<String, Object>> dataMap = new HashMap<>();
        for (Map<String, Object> map : dbList) { dataMap.put((String) map.get("key_str"), map); }

        List<String> xAxis = new ArrayList<>();
        List<Long> seriesOrder = new ArrayList<>();
        List<BigDecimal> seriesVio = new ArrayList<>();

        for (int i = ("day".equals(type) ? 0 : 1); i < ("day".equals(type) ? 24 : steps + 1); i++) {
            String key = String.format("%02d", i);
            xAxis.add(key + ("day".equals(type) ? "点" : ("month".equals(type) ? "日" : "月")));
            if (dataMap.containsKey(key)) {
                seriesOrder.add((Long) dataMap.get(key).get("total"));
                Object vio = dataMap.get(key).get("vio");
                seriesVio.add(vio == null ? BigDecimal.ZERO : (BigDecimal) vio);
            } else {
                seriesOrder.add(0L); seriesVio.add(BigDecimal.ZERO);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dates", xAxis); result.put("orders", seriesOrder); result.put("violations", seriesVio);
        return ResponseEntity.ok(result);
    }
}