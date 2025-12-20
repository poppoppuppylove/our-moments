package com.gravity.ourmoments.controller;

import com.gravity.ourmoments.service.OssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final OssService ossService;

    // 允许的图片类型
    private static final String[] ALLOWED_IMAGE_TYPES = {
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp"
    };

    // 最大文件大小 5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 上传图片（默认到 images 文件夹）
     */
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        return uploadToFolder(file, "images");
    }

    /**
     * 上传头像
     */
    @PostMapping("/upload/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return uploadToFolder(file, "avatars");
    }

    /**
     * 上传背景图
     */
    @PostMapping("/upload/background")
    public ResponseEntity<?> uploadBackground(@RequestParam("file") MultipartFile file) {
        return uploadToFolder(file, "backgrounds");
    }

    /**
     * 上传到指定文件夹
     */
    private ResponseEntity<?> uploadToFolder(MultipartFile file, String folder) {
        Map<String, Object> response = new HashMap<>();

        // 验证文件
        if (file == null || file.isEmpty()) {
            response.put("success", false);
            response.put("message", "请选择要上传的文件");
            return ResponseEntity.badRequest().body(response);
        }

        // 验证文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            response.put("success", false);
            response.put("message", "文件大小不能超过 5MB");
            return ResponseEntity.badRequest().body(response);
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (!isAllowedImageType(contentType)) {
            response.put("success", false);
            response.put("message", "只支持 JPG、PNG、GIF、WebP、BMP 格式的图片");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String url = ossService.uploadFile(file, folder);

            response.put("success", true);
            response.put("url", url);
            response.put("message", "上传成功");

            log.info("File uploaded: {} -> {}", file.getOriginalFilename(), url);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("File upload failed", e);
            response.put("success", false);
            response.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam("url") String fileUrl) {
        Map<String, Object> response = new HashMap<>();

        try {
            ossService.deleteFile(fileUrl);
            response.put("success", true);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("File deletion failed", e);
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 检查是否为允许的图片类型
     */
    private boolean isAllowedImageType(String contentType) {
        if (contentType == null) return false;
        for (String type : ALLOWED_IMAGE_TYPES) {
            if (type.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }
}
