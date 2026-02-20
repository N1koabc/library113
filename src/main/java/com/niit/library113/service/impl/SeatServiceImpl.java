package com.niit.library113.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niit.library113.dto.ReservationRequest;
import com.niit.library113.entity.CreditLog;
import com.niit.library113.entity.Reservation;
import com.niit.library113.entity.Seat;
import com.niit.library113.entity.User;
import com.niit.library113.mapper.CreditLogMapper;
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
    private final CreditLogMapper creditLogMapper;

    private final ZoneId ZONE_CN = ZoneId.of("Asia/Shanghai");
    private static boolean LIBRARY_CLOSED = false;

    // 任务1：处理未签到违约
    @Scheduled(cron = "0 * * * * ?", zone = "Asia/Shanghai")
    @Transactional(rollbackFor = Exception.class)
    public void autoCheckNoShows() {
        LocalDateTime deadline = LocalDateTime.now(ZONE_CN).minusMinutes(15);
        List<Reservation> noShows = reservationMapper.selectList(
                new QueryWrapper<Reservation>().eq("status", 0).lt("start_time", deadline)
        );
        if(noShows.isEmpty()) return;

        for (Reservation res : noShows) {
            res.setStatus(2);
            res.setEndTime(LocalDateTime.now(ZONE_CN));
            reservationMapper.updateById(res);

            Seat seat = this.getById(res.getSeatId());
            if (seat != null) { seat.setStatus(0); this.updateById(seat); }

            User user = userMapper.selectById(res.getUserId());
            if (user != null) {
                int oldScore = user.getCreditScore();
                int newScore = Math.max(0, oldScore - 5);
                user.setCreditScore(newScore); userMapper.updateById(user);
                CreditLog log = new CreditLog();
                log.setUserId(user.getId()); log.setDelta(newScore - oldScore);
                log.setReason("超时15分钟未签到自动违约");
                log.setCreateTime(LocalDateTime.now(ZONE_CN));
                creditLogMapper.insert(log);
                try { messageService.send(user.getId(), "违规扣分通知", "预约已超过15分钟未签到，座位已释放并扣除5分信用分。"); } catch(Exception e){}
            }
        }
    }

    // 【新增任务2】：自动释放到达预定结束时间(endTime)的座位
    @Scheduled(cron = "0 * * * * ?", zone = "Asia/Shanghai")
    @Transactional(rollbackFor = Exception.class)
    public void autoReleaseExpiredSeats() {
        LocalDateTime now = LocalDateTime.now(ZONE_CN);
        List<Reservation> expired = reservationMapper.selectList(
                new QueryWrapper<Reservation>().eq("status", 1).le("end_time", now)
        );
        for (Reservation res : expired) {
            res.setStatus(3); // 3 代表正常签退
            reservationMapper.updateById(res);

            Seat seat = this.getById(res.getSeatId());
            if (seat != null) {
                seat.setStatus(0); // 释放座位
                this.updateById(seat);
            }

            // 给正常完成自习的用户加分奖励
            User user = userMapper.selectById(res.getUserId());
            if(user != null && user.getCreditScore() < 100) {
                user.setCreditScore(user.getCreditScore() + 1);
                userMapper.updateById(user);
                CreditLog log = new CreditLog();
                log.setUserId(user.getId()); log.setDelta(1); log.setReason("正常完成自习自动签退");
                log.setCreateTime(LocalDateTime.now(ZONE_CN)); creditLogMapper.insert(log);
            }
        }
    }

    // 任务3：每晚22点全馆清场
    @Scheduled(cron = "0 0 22 * * ?", zone = "Asia/Shanghai")
    @Transactional(rollbackFor = Exception.class)
    public void autoReleaseAllSeats() {
        UpdateWrapper<Reservation> resUpdate = new UpdateWrapper<>();
        resUpdate.in("status", 0, 1).set("status", 3).set("end_time", LocalDateTime.now(ZONE_CN));
        reservationMapper.update(null, resUpdate);
        UpdateWrapper<Seat> seatUpdate = new UpdateWrapper<>();
        seatUpdate.eq("status", 1).set("status", 0);
        this.update(seatUpdate);
    }

    @Override
    @Transactional
    public void checkIn(Long reservationId) {
        if (LIBRARY_CLOSED) throw new RuntimeException("闭馆中");
        Reservation res = reservationMapper.selectById(reservationId);
        if (res == null || res.getStatus() != 0) throw new RuntimeException("状态无效");
        if (LocalDateTime.now(ZONE_CN).isBefore(res.getStartTime())) throw new RuntimeException("未到时间");

        res.setStatus(1);
        reservationMapper.updateById(res);

        User user = userMapper.selectById(res.getUserId());
        if (user != null) {
            user.setLastLoginTime(LocalDateTime.now(ZONE_CN)); // 签到算真实入馆时间
            int oldScore = user.getCreditScore();
            int newScore = Math.min(100, oldScore + 3);
            user.setCreditScore(newScore);
            userMapper.updateById(user);

            if (newScore > oldScore) {
                CreditLog log = new CreditLog();
                log.setUserId(user.getId()); log.setDelta(newScore - oldScore);
                log.setReason("按时签到奖励"); log.setCreateTime(LocalDateTime.now(ZONE_CN));
                creditLogMapper.insert(log);
            }
        }
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
        if(user != null && user.getCreditScore() < 100) {
            user.setCreditScore(user.getCreditScore() + 1); userMapper.updateById(user);
            CreditLog log = new CreditLog(); log.setUserId(user.getId()); log.setDelta(1); log.setReason("正常签退"); log.setCreateTime(LocalDateTime.now(ZONE_CN)); creditLogMapper.insert(log);
        }
    }

    @Override
    public List<Seat> findAvailableSeats(Integer floor, String zone, LocalDateTime checkStartTime, LocalDateTime checkEndTime) { return this.list(new QueryWrapper<Seat>().eq("floor", floor).eq("zone", zone).orderByAsc("seat_number")); }

    @Override
    @Transactional
    public boolean createReservation(ReservationRequest request) {
        if (LIBRARY_CLOSED) throw new RuntimeException("闭馆中");
        User user = userMapper.selectById(request.getUserId());
        if (user != null && user.getCreditScore() < 60) throw new RuntimeException("信用分不足60，禁止预约");
        if (reservationMapper.selectCount(new QueryWrapper<Reservation>().eq("user_id", request.getUserId()).in("status", 0, 1)) > 0) throw new RuntimeException("已有预约");
        boolean success = this.update(new UpdateWrapper<Seat>().set("status", 1).eq("id", request.getSeatId()).eq("status", 0));
        if (!success) throw new RuntimeException("座位已被抢");
        Reservation res = new Reservation(); res.setUserId(request.getUserId()); res.setSeatId(request.getSeatId());
        res.setStartTime(request.getStartTime()); res.setEndTime(request.getEndTime()); res.setStatus(0); res.setCreateTime(LocalDateTime.now(ZONE_CN));
        reservationMapper.insert(res); return true;
    }

    @Override
    @Transactional
    public boolean cancelReservation(ReservationRequest request) {
        Reservation res = reservationMapper.selectOne(new QueryWrapper<Reservation>().eq("user_id", request.getUserId()).eq("seat_id", request.getSeatId()).in("status", 0, 1).last("LIMIT 1"));
        if (res == null) throw new RuntimeException("无有效预约");
        res.setStatus(4); reservationMapper.updateById(res);
        Seat seat = this.getById(res.getSeatId());
        if (seat != null) { seat.setStatus(0); this.updateById(seat); } return true;
    }

    @Override
    public List<List<Object>> getHeatmapData() {
        List<List<Object>> result = new ArrayList<>(); if (LIBRARY_CLOSED) { for (int x = 0; x < 7; x++) { for (int y = 0; y < 5; y++) { result.add(Arrays.asList(x, y, 0)); } } return result; }
        LocalDateTime startOfDay = LocalDate.now(ZONE_CN).atStartOfDay(); LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(ZONE_CN), LocalTime.MAX);
        List<Reservation> todayReservations = reservationMapper.selectList(new QueryWrapper<Reservation>().ge("start_time", startOfDay).lt("start_time", endOfDay).in("status", 0, 1));
        List<Seat> allSeats = this.list(); Map<Long, Integer> seatFloorMap = allSeats.stream().collect(Collectors.toMap(Seat::getId, Seat::getFloor));
        int[][] matrix = new int[7][5];
        for (Reservation res : todayReservations) {
            Integer floor = seatFloorMap.get(res.getSeatId()); if (floor == null) continue;
            int yIndex = floor - 1; LocalDateTime sTime = res.getStartTime(); if (sTime.getHour() < 8) sTime = sTime.plusHours(8);
            int startSlot = Math.max(0, (sTime.getHour() - 8) / 2); if(startSlot < 7) matrix[startSlot][yIndex]++;
        }
        for (int x = 0; x < 7; x++) { for (int y = 0; y < 5; y++) { result.add(Arrays.asList(x, y, matrix[x][y])); } } return result;
    }

    @Override public List<Integer> getFloorSaturation() { List<Integer> list = new ArrayList<>(); List<Seat> all = this.list(); for(int i=1; i<=5; i++) { int f = i; long total = all.stream().filter(s->s.getFloor()==f).count(); long occ = all.stream().filter(s->s.getFloor()==f && s.getStatus()==1).count(); list.add(total==0?0:(int)(occ*100/total)); } return list; }

    @Override public List<Map<String, Object>> getLatestLogs() {
        List<Reservation> logs = reservationMapper.selectList(new QueryWrapper<Reservation>().eq("status", 1).orderByDesc("id").last("LIMIT 6"));
        List<Map<String, Object>> result = new ArrayList<>();
        for(Reservation r : logs) {
            User u = userMapper.selectById(r.getUserId()); Seat s = this.getById(r.getSeatId());
            if(u!=null && s!=null) { Map<String, Object> map = new HashMap<>(); map.put("name", u.getRealName()); map.put("avatar", u.getAvatar()); map.put("location", s.getFloor()+"F "+s.getZone()); map.put("action", "已签到入座"); map.put("time", r.getCreateTime().format(DateTimeFormatter.ofPattern("HH:mm"))); result.add(map); }
        } return result;
    }

    @Override public Map<String, Object> getLibraryOccupancy() { long total = this.count(); long occupied = this.count(new QueryWrapper<Seat>().eq("status", 1)); Map<String, Object> map = new HashMap<>(); map.put("total", total); map.put("occupied", occupied); map.put("percent", total==0?0:(int)(occupied*100.0/total)); return map; }
    @Override public Seat getMyActiveSeat(Long userId) { return null; }
    @Override public void closeLibrary() { LIBRARY_CLOSED = true; UpdateWrapper<Seat> seatUpdate = new UpdateWrapper<>(); seatUpdate.set("status", 0).ne("status", 2); this.update(seatUpdate); UpdateWrapper<Reservation> resUpdate = new UpdateWrapper<>(); resUpdate.in("status", 0, 1, 3).set("status", 4); reservationMapper.update(null, resUpdate); }
    @Override public void openLibrary() { LIBRARY_CLOSED = false; }
    @Override public boolean isLibraryClosed() { return LIBRARY_CLOSED; }

    @Override public void setSeatMaintenance(Long seatId, boolean isMaintenance) { Seat targetSeat = this.getById(seatId); if (targetSeat == null) return; if (isMaintenance) { Long checkedInCount = reservationMapper.selectCount(new QueryWrapper<Reservation>().eq("seat_id", seatId).eq("status", 1)); if (checkedInCount != null && checkedInCount > 0) throw new RuntimeException("该座位有人正在使用，无法维护！"); List<Reservation> conflicts = reservationMapper.selectList(new QueryWrapper<Reservation>().eq("seat_id", seatId).eq("status", 0)); for (Reservation res : conflicts) { Seat newSeat = this.getOne(new QueryWrapper<Seat>().eq("status", 0).last("LIMIT 1"), false); if (newSeat != null) { newSeat.setStatus(1); this.updateById(newSeat); res.setSeatId(newSeat.getId()); reservationMapper.updateById(res); try { messageService.send(res.getUserId(), "调座通知", "原座位维护，已调至 " + newSeat.getSeatNumber()); } catch(Exception e){} } else throw new RuntimeException("全馆无座可换"); } targetSeat.setStatus(2); } else if (targetSeat.getStatus() == 2) targetSeat.setStatus(0); this.updateById(targetSeat); }

    @Override public void batchMaintenance(Integer floor, String zone, boolean isMaintenance) { QueryWrapper<Seat> query = new QueryWrapper<>(); if (floor != null) query.eq("floor", floor); if (zone != null && !"全部".equals(zone)) query.eq("zone", zone); List<Seat> targetSeats = this.list(query); if (targetSeats.isEmpty()) return; List<Long> targetSeatIds = targetSeats.stream().map(Seat::getId).collect(Collectors.toList()); if (isMaintenance) { Long checkedInCount = reservationMapper.selectCount(new QueryWrapper<Reservation>().in("seat_id", targetSeatIds).eq("status", 1)); if (checkedInCount != null && checkedInCount > 0) throw new RuntimeException("该区域内有人正在使用，无法批量维护！"); UpdateWrapper<Seat> updateTarget = new UpdateWrapper<>(); updateTarget.in("id", targetSeatIds).set("status", 2); this.update(updateTarget); } else { UpdateWrapper<Seat> updateTarget = new UpdateWrapper<>(); updateTarget.in("id", targetSeatIds).eq("status", 2).set("status", 0); this.update(updateTarget); } }
}