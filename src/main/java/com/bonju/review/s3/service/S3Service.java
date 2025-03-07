package com.bonju.review.s3.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadAndGetUrl(MultipartFile file);
    void deleteFile(String fileName);
}