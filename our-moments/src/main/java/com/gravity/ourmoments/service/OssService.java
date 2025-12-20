package com.gravity.ourmoments.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.gravity.ourmoments.config.OssConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OssService {

    private final OSS ossClient;
    private final OssConfig ossConfig;

    /**
     * 上传文件到 OSS
     * @param file 上传的文件
     * @param folder 存储文件夹（如 "images", "avatars"）
     * @return 文件访问 URL
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            InputStream inputStream = file.getInputStream();

            // 生成文件路径：folder/yyyy/MM/dd/uuid.ext
            String originalFilename = file.getOriginalFilename();
            String suffix = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
            String objectKey = folder + "/" + datePath + "/" + fileName;

            // 设置元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(getContentType(suffix));
            metadata.setContentLength(file.getSize());

            // 上传文件
            ossClient.putObject(ossConfig.getBucketName(), objectKey, inputStream, metadata);

            log.info("File uploaded successfully: {}", objectKey);

            // 返回完整 URL
            return ossConfig.getUrlPrefix() + objectKey;

        } catch (Exception e) {
            log.error("Failed to upload file to OSS", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件（默认到 images 文件夹）
     */
    public String uploadFile(MultipartFile file) {
        return uploadFile(file, "images");
    }

    /**
     * 删除 OSS 文件
     * @param fileUrl 文件的完整 URL
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(ossConfig.getUrlPrefix())) {
            return;
        }
        String objectKey = fileUrl.replace(ossConfig.getUrlPrefix(), "");
        deleteByKey(objectKey);
    }

    /**
     * 根据文件 key 删除文件
     * @param objectKey 文件在 OSS 中的 key
     */
    public void deleteByKey(String objectKey) {
        try {
            ossClient.deleteObject(ossConfig.getBucketName(), objectKey);
            log.info("File deleted successfully: {}", objectKey);
        } catch (Exception e) {
            log.error("Failed to delete file from OSS: {}", objectKey, e);
        }
    }

    /**
     * 根据文件后缀获取 Content-Type
     */
    private String getContentType(String suffix) {
        if (suffix == null) return "application/octet-stream";
        suffix = suffix.toLowerCase();
        return switch (suffix) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".webp" -> "image/webp";
            case ".bmp" -> "image/bmp";
            case ".svg" -> "image/svg+xml";
            case ".mp4" -> "video/mp4";
            case ".mp3" -> "audio/mpeg";
            case ".pdf" -> "application/pdf";
            default -> "application/octet-stream";
        };
    }
}
