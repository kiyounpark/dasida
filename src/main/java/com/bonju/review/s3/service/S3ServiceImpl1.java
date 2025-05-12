package com.bonju.review.s3.service;

import com.bonju.review.s3.dto.ImageResponseDto;
import com.bonju.review.s3.infra.ObjectKey;
import com.bonju.review.s3.infra.ImageProvider;
import com.bonju.review.s3.infra.ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl1 {

  private final ImageUploader imageUploader;
  private final ImageProvider imageProvider;
  public ImageResponseDto generatePublicUrl(MultipartFile file) {

    ObjectKey uploadKey = imageUploader.upload(file);
    String publicUrl = imageProvider.getPublicUrl(uploadKey);
    return new ImageResponseDto(publicUrl);
  }
}
