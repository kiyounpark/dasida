package com.bonju.review.image.service;

import com.bonju.review.image.dto.ImageResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
  ImageResponseDto uploadAndGetPublicUrl(MultipartFile file);
}
