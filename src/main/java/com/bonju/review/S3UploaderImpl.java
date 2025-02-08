package com.bonju.review;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class S3UploaderImpl implements S3Uploader {
    @Override
    public void uploadImage(MultipartFile file) {

    }
}
