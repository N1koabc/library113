package com.niit.library113.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.niit.library113.dto.AiSeatParams;
import com.niit.library113.entity.Seat;
import com.niit.library113.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/ai")
public class AiController {

    // 【核心整合】：把你毕设里的 SeatService 注入进来！
    @Autowired
    private SeatService seatService;

    // 填入你刚才在智谱申请的免费 API_KEY
    private static final String API_KEY = "fb115d774d364c1182d2704de4fa13ab.ihAqsP90l6GHnahK";
    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";

    @PostMapping("/search-smart")
    public Map<String, Object> searchSmartSeats(@RequestBody String userMessage) {

        // ================= 1. AI 意图解析阶段 =================
        String systemPrompt = "你是一个专门为桂电图书馆座位预约系统设计的“意图解析引擎”。\n" +
                "你的唯一工作是将用户的自然语言输入，精确提取为结构化的 JSON 格式。\n" +
                "【提取规则与字段定义】\n" +
                "- date: 预约日期(YYYY-MM-DD)，假设今天是2026-03-02。\n" +
                "- time_period: 时间段，仅限 morning, afternoon, evening, all_day。\n" +
                "- floor: 楼层，整型数字。\n" +
                "- has_socket: 是否需要插座(true/false)。注意推理：若用户提到“带电脑”、“充电”等场景，判定为 true。\n" +
                "- has_window: 是否需要靠窗(true/false)。\n" +
                "【极其重要的输出限制】\n" +
                "绝对不要输出任何多余的解释，不要带 ```json 标记，必须且只能输出纯 JSON 对象！如果未提及设为 null。";

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "glm-4-flash");

        JSONArray messages = new JSONArray();

        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.1);

        // 呼叫大模型
        HttpResponse response = HttpRequest.post(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .body(requestBody.toJSONString())
                .execute();

        if (!response.isOk()) {
            throw new RuntimeException("大模型服务开小差了: " + response.body());
        }

        // ================= 2. 数据库查询阶段 =================
        // 剥离出干净的 JSON 字符串
        JSONObject resJson = JSON.parseObject(response.body());
        String aiResultStr = resJson.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        // 将 JSON 字符串完美映射成我们的新员工 DTO
        AiSeatParams params = JSON.parseObject(aiResultStr, AiSeatParams.class);

        // 调用我们在 SeatService 里写好的动态查询逻辑，去数据库里捞座位！
        List<Seat> smartSeats = seatService.findSmartSeats(params);

        // ================= 3. 组装豪华返回包 =================
        // 我们不光返回座位，还把 AI 解析出的意图一起返回去给前端炫技
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("parsedIntent", params);   // 告诉前端：AI 听懂了什么
        resultMap.put("seatList", smartSeats);   // 告诉前端：查到了哪些座位

        return resultMap;
    }
}