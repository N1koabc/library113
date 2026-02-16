package com.niit.library113.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.net.InetAddress;

@RestController
public class DebugController {

    @GetMapping("/debug")
    public String diagnose() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: sans-serif; padding: 20px;'>");
        sb.append("<h1>🕵️‍♂️ 系统路径诊断报告</h1>");

        // 1. 获取系统工作目录
        String userDir = System.getProperty("user.dir");
        sb.append("<h3>1. 项目运行根目录 (user.dir)</h3>");
        sb.append("<div style='background: #f0f0f0; padding: 10px; border-radius: 5px;'>").append(userDir).append("</div>");

        // 2. 检查 uploads 文件夹
        File uploadDir = new File(userDir, "uploads");
        sb.append("<h3>2. Uploads 文件夹状态</h3>");
        sb.append("<ul>");
        sb.append("<li><strong>预期完整路径: </strong> <span style='color:blue'>").append(uploadDir.getAbsolutePath()).append("</span></li>");

        if (uploadDir.exists()) {
            sb.append("<li><strong>文件夹是否存在: </strong> <span style='color:green; font-weight:bold;'>✅ 存在 (正确)</span></li>");
            String[] files = uploadDir.list();
            int count = (files == null) ? 0 : files.length;
            sb.append("<li><strong>包含文件数量: </strong> ").append(count).append(" 个</li>");
            if (count > 0) {
                sb.append("<li><strong>最新文件名示例: </strong> ").append(files[0]).append("</li>");
            }
        } else {
            sb.append("<li><strong>文件夹是否存在: </strong> <span style='color:red; font-weight:bold;'>❌ 不存在 (错误！)</span></li>");
            sb.append("<li><strong style='color:red'>解决办法: </strong> 请务必复制上面的“预期完整路径”，在您的电脑文件管理器中找到这个位置，并手动新建 'uploads' 文件夹。</li>");
        }
        sb.append("</ul>");

        // 3. 图片访问测试
        sb.append("<h3>3. 图片访问链接测试</h3>");
        sb.append("<p>如果上面显示有文件，请尝试点击下方链接看能否打开图片：</p>");
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            sb.append("<a href='http://localhost:8080/images/test.png' target='_blank'>http://localhost:8080/images/test.png</a> (请确保文件夹里有一张名为 test.png 的图来测试)");
        } catch (Exception e) {}

        sb.append("</body></html>");
        return sb.toString();
    }
}