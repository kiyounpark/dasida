package com.bonju.review.image.storage.service;

import com.bonju.review.image.dto.ImageResponseDto;
import com.bonju.review.image.storage.provider.ImagePublicUrlProvider;
import com.bonju.review.image.storage.uploader.ImageStorageUploader;
import com.bonju.review.image.storage.objectkey.ObjectKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {
  private final ImageStorageUploader imageUploader;
  private final ImagePublicUrlProvider imagePublicUrlProvider;

  private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp");
  private static final Set<String> ALLOWED_MIME_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

  @Override
  public ImageResponseDto uploadAndGetPublicUrl(MultipartFile file) {
    ObjectKey uploadKey = imageUploader.upload(file);
    String publicUrl = imagePublicUrlProvider.getPublicUrl(uploadKey);

    return new ImageResponseDto(publicUrl);
  }


}
