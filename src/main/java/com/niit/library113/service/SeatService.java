package com.niit.library113.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niit.library113.dto.ReservationRequest;
import com.niit.library113.entity.Seat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SeatService extends IService<Seat> {
    List<Seat> findAvailableSeats(Integer floor, String zone, LocalDateTime checkStartTime, LocalDateTime checkEndTime);
    boolean createReservation(ReservationRequest request);
    boolean cancelReservation(ReservationRequest request);
    void checkIn(Long reservationId);
    void checkOut(Long reservationId);
    Seat getMyActiveSeat(Long userId);

    // 数据大屏与管理
    List<List<Object>> getHeatmapData();
    List<Integer> getFloorSaturation();
    List<Map<String, Object>> getLatestLogs();
    Map<String, Object> getLibraryOccupancy();

    // 核心管控
    void setSeatMaintenance(Long seatId, boolean isMaintenance);
    void closeLibrary();
    void openLibrary();
    boolean isLibraryClosed();
}