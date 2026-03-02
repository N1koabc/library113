package com.niit.library113.dto;

import lombok.Data;

@Data
public class AiSeatParams {
    private String date;          // 对应 AI 的 "2026-03-03"
    private String time_period;   // 对应 AI 的 "evening"
    private Integer floor;        // 对应 AI 的 3
    private Boolean has_socket;   // 对应 AI 的 true/false
    private Boolean has_window;   // 对应 AI 的 true/false
}