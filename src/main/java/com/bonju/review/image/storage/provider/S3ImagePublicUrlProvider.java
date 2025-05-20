package com.bonju.review.image.storage.provider;

import com.bonju.review.image.config.S3Properties;
import com.bonju.review.image.exception.errorcode.StorageErrorCode;
import com.bonju.review.image.exception.exception.StorageException;
import com.bonju.review.image.storage.objectkey.ObjectKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;

import java.net.URL;

@Component
@RequiredArgsConstructor
public class S3ImagePublicUrlProvider implements ImagePublicUrlProvider {

  private final S3Client s3Client;
  private final S3Properties s3Properties;

  @Override
  public String getPublicUrl(ObjectKey objectKey) {
    String bucket = s3Properties.getBucket();
    GetUrlRequest req = GetUrlRequest.builder()
            .bucket(bucket)
            .key(objectKey.value())
            .build();

    try {
      URL url = s3Client.utilities().getUrl(req);
      if (url == null) {
        throw new StorageException(StorageErrorCode.RETRIEVE_FAILED);
      }
      return url.toString();
    } catch (SdkClientException e) {
      throw new StorageException(StorageErrorCode.RETRIEVE_FAILED, e);
    }
  }
}
