package com.bonju.review.image.service;

import com.bonju.review.image.dto.ImageResponseDto;
import com.bonju.review.image.exception.errorcode.ImageErrorCode;
import com.bonju.review.image.exception.exception.ImageException;
import com.bonju.review.image.storage.service.StorageService;
import com.bonju.review.util.FileExtensionExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

  private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp");
  private static final Set<String> ALLOWED_MIME_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

  private final StorageService storageService;
  @Override
  public ImageResponseDto uploadAndGetPublicUrl(MultipartFile file) {
    validateImageFile(file);

    return storageService.uploadAndGetPublicUrl(file);
  }

  private void validateImageFile(MultipartFile file) {
    String extension = FileExtensionExtractor.extract(file.getOriginalFilename()).toLowerCase();
    String contentType = file.getContentType();

    if (!ALLOWED_EXTENSIONS.contains(extension)) {
      throw new ImageException(ImageErrorCode.INVALID_EXTENSION);
    }
    if (!ALLOWED_MIME_TYPES.contains(contentType)) {
      throw new ImageException(ImageErrorCode.INVALID_MIME_TYPE);
    }
  }
}
