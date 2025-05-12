package com.bonju.review.s3.infra;
import org.springframework.web.multipart.MultipartFile;

public interface ImageUploader {
  ObjectKey upload(MultipartFile file);
}
