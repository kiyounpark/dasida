package com.bonju.review.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Client {
    String uploadToS3(MultipartFile file);
}
