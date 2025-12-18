package com.gravity.ourmoments.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class OssService {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.url-prefix}")
    private String urlPrefix;

    public String uploadFile(MultipartFile file) {
        try {
            // Get input stream
            InputStream inputStream = file.getInputStream();

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;

            // Create OSSClient
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // Upload
            ossClient.putObject(bucketName, fileName, inputStream);

            // Close
            ossClient.shutdown();

            // Return URL
            return urlPrefix + fileName;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
