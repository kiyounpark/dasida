package com.bonju.review.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    String createImageUrl(MultipartFile multipartFile);

    void uploadToS3(MultipartFile multipartFile);
}
