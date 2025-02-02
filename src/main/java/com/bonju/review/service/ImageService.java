package com.bonju.review.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    String getImageUrl(MultipartFile multipartFile);
}
