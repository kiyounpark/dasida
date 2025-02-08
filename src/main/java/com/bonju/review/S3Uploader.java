package com.bonju.review;

import org.springframework.web.multipart.MultipartFile;

public interface S3Uploader {
    void uploadImage(MultipartFile file);
}
