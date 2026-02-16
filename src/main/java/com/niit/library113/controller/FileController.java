package com.niit.library113.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class FileController {

    // 使用 Paths 获取当前项目的绝对路径，更安全
    // 这里指向项目根目录下的 uploads 文件夹
    private final Path UPLOAD_PATH = Paths.get(System.getProperty("user.dir"), "uploads");

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件不能为空");
        }

        try {
            // 1. 确保目录存在 (如果不存在则自动创建)
            if (!Files.exists(UPLOAD_PATH)) {
                Files.createDirectories(UPLOAD_PATH);
                System.out.println("【系统日志】已自动创建上传目录: " + UPLOAD_PATH.toAbsolutePath());
            }

            // 2. 生成新文件名 (UUID防止重名)
            String originalFilename = file.getOriginalFilename();
            String suffix = (originalFilename != null && originalFilename.contains("."))
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".png";
            String newFilename = UUID.randomUUID().toString() + suffix;

            // 3. 保存文件 (Resolve 自动处理 Windows/Linux 路径分隔符)
            Path dest = UPLOAD_PATH.resolve(newFilename);
            file.transferTo(dest.toFile());

            System.out.println("【上传成功】真实存储路径: " + dest.toAbsolutePath());

            // 4. 返回相对路径 URL
            // 前端访问时会自动拼接为 http://localhost:8080/images/xxx.png
            String fileUrl = "/images/" + newFilename;
            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("上传失败: " + e.getMessage());
        }
    }
}