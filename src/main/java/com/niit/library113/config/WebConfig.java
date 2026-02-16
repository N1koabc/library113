package com.niit.library113.config;

import com.niit.library113.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    // 1. 全局跨域配置
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .allowedHeaders("*")
                .maxAge(3600);
    }

    // 2. 注册拦截器 (核心修复：放行公共数据接口)
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        // 登录注册
                        "/api/user/login",
                        "/api/user/register",
                        "/api/upload",
                        "/error",
                        "/images/**",

                        // 【新增】放行看板公共数据 (修复数据不显示的问题)
                        "/api/notices/**",       // 公告
                        "/api/seats/list",       // 座位列表
                        "/api/seats/stats",      // 热力图
                        "/api/seats/saturation", // 饱和度
                        "/api/seats/logs",       // 实时动态
                        "/api/seats/occupancy"   // 在馆人数
                );
    }

    // 3. 静态资源映射 (保持图片上传功能正常)
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = Paths.get(System.getProperty("user.dir"), "uploads").toUri().toString();
        if (!path.endsWith("/")) {
            path += "/";
        }
        System.out.println("【系统日志】图片资源映射路径: " + path);
        registry.addResourceHandler("/images/**")
                .addResourceLocations(path);
    }
}