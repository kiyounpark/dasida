package com.bonju.review.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadAndGetUrl(MultipartFile file); // ✅ 업로드 + URL 반환
    void deleteFile(String fileName);
}