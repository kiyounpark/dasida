package com.bonju.review.image.storage.service;

import com.bonju.review.image.dto.ImageResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
  ImageResponseDto uploadAndGetPublicUrl(MultipartFile file);
}
