package com.bonju.review.image.controller;

import com.bonju.review.image.dto.ImageResponseDto;
import com.bonju.review.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {
  private final ImageService imageService;

  // ✅ 파일 업로드 후 JSON 형태로 URL 반환
  @PostMapping
  public ImageResponseDto uploadFile(@RequestParam("file") MultipartFile file) {
    return imageService.uploadAndGetPublicUrl(file);
  }
}