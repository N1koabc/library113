package com.niit.library113;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // 1. 引入

@SpringBootApplication
@EnableScheduling // 2. 开启定时任务开关
@MapperScan("com.niit.library113.mapper")
public class Library113Application {
    public static void main(String[] args) {
        SpringApplication.run(Library113Application.class, args);
    }
}