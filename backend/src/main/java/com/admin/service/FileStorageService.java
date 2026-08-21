package com.admin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件存储服务：保存/删除上传文件，返回可访问的 URL（/uploads/...）
 */
@Service
public class FileStorageService {

    @Value("${admin.upload.dir:./uploads}")
    private String uploadDir;

    /** 保存文件，返回相对 URL */
    public String save(MultipartFile file) throws IOException {
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);
        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());
        return "/uploads/" + filename;
    }

    /** 根据 URL 删除文件 */
    public void deleteByUrl(String url) {
        if (url == null || url.isBlank()) {
            return;
        }
        try {
            String prefix = "/uploads/";
            if (url.startsWith(prefix)) {
                String filename = url.substring(prefix.length());
                Path path = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(filename);
                Files.deleteIfExists(path);
            }
        } catch (IOException ignored) {
            // 删除失败不阻断业务
        }
    }
}
