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
    List<List<Object>> getHeatmapData();
    List<Integer> getFloorSaturation();
    List<Map<String, Object>> getLatestLogs();
    Map<String, Object> getLibraryOccupancy();
    void setSeatMaintenance(Long seatId, boolean isMaintenance);
    void closeLibrary();
    void openLibrary();
    boolean isLibraryClosed();

    // 【本次新增】批量维护接口
    void batchMaintenance(Integer floor, String zone, boolean isMaintenance);
}