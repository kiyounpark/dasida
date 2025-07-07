package com.bonju.review.image.service;

import com.bonju.review.image.dto.ImageResponseDto;
import com.bonju.review.image.exception.errorcode.ImageErrorCode;
import com.bonju.review.image.exception.exception.ImageException;
import com.bonju.review.image.storage.uploader.ImageStorageUploader;
import com.bonju.review.image.storage.objectkey.ObjectKey;
import com.bonju.review.image.storage.provider.ImagePublicUrlProvider;
import com.bonju.review.util.FileExtensionExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl {

  private final ImageStorageUploader imageUploader;
  private final ImagePublicUrlProvider imagePublicUrlProvider;

  private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp");
  private static final Set<String> ALLOWED_MIME_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
  public ImageResponseDto generatePublicUrl(MultipartFile file) {
    validateImageFile(file);

    ObjectKey uploadKey = imageUploader.upload(file);
    String publicUrl = imagePublicUrlProvider.getPublicUrl(uploadKey);
    return new ImageResponseDto(publicUrl);
  }

  private void validateImageFile(MultipartFile file) {
    if (isNotSupportedFile(file)) {
      throw new ImageException(ImageErrorCode.INVALID_EXTENSION);
    }
  }

  private boolean isNotSupportedFile(MultipartFile file) {
    String extension = FileExtensionExtractor.extract(file.getOriginalFilename()).toLowerCase();
    String contentType = file.getContentType();

    return !ALLOWED_EXTENSIONS.contains(extension) || !ALLOWED_MIME_TYPES.contains(contentType);
  }
}
