package com.bonju.review.service;

import com.bonju.review.dto.ImageRequestDto;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    String getImageUrl(ImageRequestDto imageRequestDto);
}
