package com.bonju.review;

import org.springframework.web.multipart.MultipartFile;

public interface S3UrlProvider {

    String createImageUrl(MultipartFile file);
}
