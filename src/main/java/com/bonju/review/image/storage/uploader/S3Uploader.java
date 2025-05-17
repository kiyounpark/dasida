package com.bonju.review.image.storage.uploader;

import com.bonju.review.image.storage.objectkey.ObjectKey;
import com.bonju.review.image.storage.objectkey.ObjectKeyGenerator;
import com.bonju.review.image.exception.errorcode.StorageErrorCode;
import com.bonju.review.image.exception.exception.StorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;

import static software.amazon.awssdk.services.s3.model.ObjectCannedACL.PUBLIC_READ;

@Component
@RequiredArgsConstructor
public class S3Uploader implements ImageStorageUploader {

  private final S3Client s3Client;

  private final ObjectKeyGenerator objectKeyGenerator;

  @Value("${cloud.aws.s3.bucket}")
  String bucket;

  @Override
  public ObjectKey upload(MultipartFile file) {
    String objectKey = objectKeyGenerator.generate(file.getOriginalFilename());

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .acl(PUBLIC_READ)
            .key(objectKey)
            .bucket(bucket)
            .contentType(file.getContentType())
            .build();

    try (InputStream inputStream = file.getInputStream()) {
      RequestBody requestBody = RequestBody.fromInputStream(inputStream, file.getSize());
      s3Client.putObject(putObjectRequest, requestBody);
    } catch (IOException | S3Exception | SdkClientException e) {
      throw new StorageException(StorageErrorCode.UPLOAD_FAILED, e);
    }

    return new ObjectKey(objectKey);
  }
}
