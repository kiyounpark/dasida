package com.bonju.review.image.storage.uploader;

import com.bonju.review.image.storage.objectkey.ObjectKey;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageUploader {
  ObjectKey upload(MultipartFile file);
}
