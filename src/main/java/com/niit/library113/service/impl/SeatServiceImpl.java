package com.niit.library113.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niit.library113.dto.ReservationRequest;
import com.niit.library113.entity.Reservation;
import com.niit.library113.entity.Seat;
import com.niit.library113.entity.User;
import com.niit.library113.mapper.ReservationMapper;
import com.niit.library113.mapper.SeatMapper;
import com.niit.library113.mapper.UserMapper;
import com.niit.library113.service.MessageService;
import com.niit.library113.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl extends ServiceImpl<SeatMapper, Seat> implements SeatService {

    private final ReservationMapper reservationMapper;
    private final UserMapper userMapper;
    private final MessageService messageService;

    private final ZoneId ZONE_CN = ZoneId.of("Asia/Shanghai");
    private static boolean LIBRARY_CLOSED = false;

    @Override
    public List<Seat> findAvailableSeats(Integer floor, String zone, LocalDateTime checkStartTime, LocalDateTime checkEndTime) {
        return this.list(new QueryWrapper<Seat>().eq("floor", floor).eq("zone", zone).orderByAsc("seat_number"));
    }

    // 每日深夜重置
    @Scheduled(cron = "0 0 23 * * ?", zone = "Asia/Shanghai")
    @Transactional
    public void systemAutoReleaseAll() {
        UpdateWrapper<Reservation> resUpdate = new UpdateWrapper<>();
        resUpdate.in("status", 0, 1).set("status", 3).set("end_time", LocalDateTime.now(ZONE_CN));
        reservationMapper.update(null, resUpdate);

        UpdateWrapper<Seat> seatUpdate = new UpdateWrapper<>();
        seatUpdate.eq("status", 1).set("status", 0);
        this.update(seatUpdate);
    }

    @Override
    @Transactional
    public boolean createReservation(ReservationRequest request) {
        if (LIBRARY_CLOSED) throw new RuntimeException("当前图书馆已闭馆，暂停预约");
        LocalDateTime now = LocalDateTime.now(ZONE_CN);
        if (request.getStartTime().isBefore(now)) throw new RuntimeException("不能预约过去的时间");
        if (reservationMapper.selectCount(new QueryWrapper<Reservation>().eq("user_id", request.getUserId()).in("status", 0, 1)) > 0) throw new RuntimeException("您已有进行中的预约");
        boolean success = this.update(new UpdateWrapper<Seat>().set("status", 1).eq("id", request.getSeatId()).eq("status", 0));
        if (!success) throw new RuntimeException("手慢了，座位已被抢占或正在维护");
        Reservation res = new Reservation();
        res.setUserId(request.getUserId()); res.setSeatId(request.getSeatId()); res.setStartTime(request.getStartTime()); res.setEndTime(request.getEndTime()); res.setStatus(0); res.setCreateTime(now);
        reservationMapper.insert(res);
        return true;
    }

    @Override
    @Transactional
    public boolean cancelReservation(ReservationRequest request) {
        Reservation res = reservationMapper.selectOne(new QueryWrapper<Reservation>().eq("user_id", request.getUserId()).eq("seat_id", request.getSeatId()).in("status", 0, 1).last("LIMIT 1"));
        if (res == null) throw new RuntimeException("未找到有效预约");
        res.setStatus(4); reservationMapper.updateById(res);
        Seat seat = this.getById(res.getSeatId());
        if (seat != null) { seat.setStatus(0); this.updateById(seat); }
        return true;
    }

    @Override
    @Transactional
    public void checkIn(Long reservationId) {
        if (LIBRARY_CLOSED) throw new RuntimeException("闭馆中，无法签到");
        Reservation res = reservationMapper.selectById(reservationId);
        if (res == null || res.getStatus() != 0) throw new RuntimeException("预约状态无效");
        if (LocalDateTime.now(ZONE_CN).isBefore(res.getStartTime())) throw new RuntimeException("未到预约时间，请稍后再试");
        res.setStatus(1); reservationMapper.updateById(res);
    }

    @Override
    @Transactional
    public void checkOut(Long reservationId) {
        Reservation res = reservationMapper.selectById(reservationId);
        if (res == null || res.getStatus() != 1) throw new RuntimeException("未签到");
        res.setStatus(3); res.setEndTime(LocalDateTime.now(ZONE_CN)); reservationMapper.updateById(res);
        Seat seat = this.getById(res.getSeatId());
        if (seat != null) { seat.setStatus(0); this.updateById(seat); }
        User user = userMapper.selectById(res.getUserId());
        if(user != null && user.getCreditScore() < 100) { user.setCreditScore(user.getCreditScore()+1); userMapper.updateById(user); }
    }

    @Override
    public Seat getMyActiveSeat(Long userId) { return null; }

    // --- 热力图与统计 ---
    @Override
    public List<List<Object>> getHeatmapData() {
        List<List<Object>> result = new ArrayList<>();
        if (LIBRARY_CLOSED) {
            for (int x = 0; x < 7; x++) { for (int y = 0; y < 5; y++) { List<Object> item = new ArrayList<>(); item.add(x); item.add(y); item.add(0); result.add(item); } }
            return result;
        }
        LocalDateTime startOfDay = LocalDate.now(ZONE_CN).atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(ZONE_CN), LocalTime.MAX);
        List<Reservation> todayReservations = reservationMapper.selectList(new QueryWrapper<Reservation>().ge("start_time", startOfDay).lt("start_time", endOfDay).in("status", 0, 1));
        List<Seat> allSeats = this.list();
        Map<Long, Integer> seatFloorMap = allSeats.stream().collect(Collectors.toMap(Seat::getId, Seat::getFloor));
        int[][] matrix = new int[7][5];
        for (Reservation res : todayReservations) {
            Integer floor = seatFloorMap.get(res.getSeatId());
            if (floor == null) continue;
            int yIndex = floor - 1;
            LocalDateTime sTime = res.getStartTime();
            if (sTime.getHour() < 8) sTime = sTime.plusHours(8);
            int startSlot = Math.max(0, (sTime.getHour() - 8) / 2);
            if(startSlot < 7) matrix[startSlot][yIndex]++;
        }
        for (int x = 0; x < 7; x++) { for (int y = 0; y < 5; y++) { List<Object> item = new ArrayList<>(); item.add(x); item.add(y); item.add(matrix[x][y]); result.add(item); } }
        return result;
    }

    @Override
    public List<Integer> getFloorSaturation() {
        List<Integer> list = new ArrayList<>();
        List<Seat> all = this.list();
        for(int i=1; i<=5; i++) {
            int f = i;
            long total = all.stream().filter(s->s.getFloor()==f).count();
            long occ = all.stream().filter(s->s.getFloor()==f && s.getStatus()==1).count();
            list.add(total==0?0:(int)(occ*100/total));
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getLatestLogs() {
        List<Reservation> logs = reservationMapper.selectList(new QueryWrapper<Reservation>().orderByDesc("create_time").last("LIMIT 6"));
        List<Map<String, Object>> result = new ArrayList<>();
        for(Reservation r : logs) {
            User u = userMapper.selectById(r.getUserId());
            Seat s = this.getById(r.getSeatId());
            if(u!=null && s!=null) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", u.getRealName()); map.put("avatar", u.getAvatar()); map.put("location", s.getFloor()+"F "+s.getZone());
                String action = "预约了"; if(r.getStatus()==1) action="使用中"; if(r.getStatus()==2) action="违约了"; if(r.getStatus()==3) action="完成了"; if(r.getStatus()==4) action="取消了";
                map.put("action", action); map.put("time", r.getCreateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                result.add(map);
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> getLibraryOccupancy() {
        long total = this.count();
        long occupied = this.count(new QueryWrapper<Seat>().eq("status", 1));
        Map<String, Object> map = new HashMap<>();
        map.put("total", total); map.put("occupied", occupied);
        map.put("percent", total==0?0:(int)(occupied*100.0/total));
        return map;
    }

    // ================== 核心功能修复：智能维护 ==================
    @Override
    @Transactional
    public void setSeatMaintenance(Long seatId, boolean isMaintenance) {
        Seat targetSeat = this.getById(seatId);
        if (targetSeat == null) return;

        if (isMaintenance) {
            // 设为维护：需要处理冲突
            List<Reservation> conflicts = reservationMapper.selectList(new QueryWrapper<Reservation>().eq("seat_id", seatId).in("status", 0, 1));

            for (Reservation res : conflicts) {
                // 1. 尝试找替补座位
                Seat newSeat = this.getOne(new QueryWrapper<Seat>().eq("floor", targetSeat.getFloor()).eq("zone", targetSeat.getZone()).eq("status", 0).last("LIMIT 1"), false);
                if (newSeat == null) newSeat = this.getOne(new QueryWrapper<Seat>().eq("floor", targetSeat.getFloor()).eq("status", 0).last("LIMIT 1"), false);
                if (newSeat == null) newSeat = this.getOne(new QueryWrapper<Seat>().eq("status", 0).last("LIMIT 1"), false);

                if (newSeat != null) {
                    newSeat.setStatus(1);
                    this.updateById(newSeat);
                    res.setSeatId(newSeat.getId());
                    reservationMapper.updateById(res);
                    try { messageService.send(res.getUserId(), "座位调整通知", "抱歉，您原座位 " + targetSeat.getSeatNumber() + " 需维护，系统已自动调整至 " + newSeat.getSeatNumber()); } catch(Exception e){}
                } else {
                    throw new RuntimeException("操作拒绝：该座位有预约且全馆已满，无座可调剂！");
                }
            }
            targetSeat.setStatus(2); // 设为维修
        } else {
            // 【核心修复】解除维护时：
            // 只有当座位是“维修中(2)”时，才恢复为“空闲(0)”
            // 如果座位是“使用中(1)”，说明有人在用，绝对不能重置！
            if (targetSeat.getStatus() == 2) {
                targetSeat.setStatus(0);
            }
        }
        this.updateById(targetSeat);
    }

    @Override
    @Transactional
    public void closeLibrary() {
        LIBRARY_CLOSED = true;
        // 释放所有座位，但排除状态为 2 (维修) 的座位
        UpdateWrapper<Seat> seatUpdate = new UpdateWrapper<>();
        seatUpdate.set("status", 0).ne("status", 2);
        this.update(seatUpdate);

        UpdateWrapper<Reservation> resUpdate = new UpdateWrapper<>();
        resUpdate.in("status", 0, 1, 3).set("status", 4);
        reservationMapper.update(null, resUpdate);
    }

    @Override
    public void openLibrary() {
        LIBRARY_CLOSED = false;
    }

    @Override
    public boolean isLibraryClosed() {
        return LIBRARY_CLOSED;
    }
}