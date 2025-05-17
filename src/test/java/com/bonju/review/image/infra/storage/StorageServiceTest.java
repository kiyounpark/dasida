package com.bonju.review.image.infra.storage;

import com.bonju.review.image.dto.ImageResponseDto;
import com.bonju.review.image.exception.errorcode.StorageErrorCode;
import com.bonju.review.image.exception.exception.StorageException;
import com.bonju.review.image.storage.objectkey.ObjectKey;
import com.bonju.review.image.storage.provider.ImagePublicUrlProvider;
import com.bonju.review.image.storage.service.S3StorageService;
import com.bonju.review.image.storage.uploader.ImageStorageUploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StorageServiceTest {
  private static final String FIELD_NAME = "image";
  private static final byte[] FILE_CONTENT = "mock image content".getBytes();
  private static final String OBJECT_KEY = "images/profile_123456.png";
  private static final String PUBLIC_URL = "https://your-bucket.s3.amazonaws.com/" + OBJECT_KEY;
  private static final String FILE_NAME = "profile.png";
  private static final String CONTENT_TYPE = "image/png";

  @Mock
  ImageStorageUploader imageUploader;

  @Mock
  ImagePublicUrlProvider imagePublicUrlProvider;

  @InjectMocks
  S3StorageService s3Service;

  @Nested
  @DisplayName("generatePublicUrl 성공 케이스")
  class Success {

    @DisplayName("S3에 이미지를 등록하고 publicUrl 을 반환한다")
    @Test
    void uploadImage_returnsPublicUrl() {
      // given
      MockMultipartFile file = createMockMultipartFile();
      ObjectKey objectKey = new ObjectKey(OBJECT_KEY);

      given(imageUploader.upload(file)).willReturn(objectKey);
      given(imagePublicUrlProvider.getPublicUrl(objectKey)).willReturn(PUBLIC_URL);

      // when
      ImageResponseDto imageResponseDto = s3Service.uploadAndGetPublicUrl(file);

      // then
      assertThat(imageResponseDto.imageUrl()).isEqualTo(PUBLIC_URL);
    }
  }

  @Nested
  @DisplayName("generatePublicUrl 실패 케이스")
  class Failure {

    @DisplayName("ImageUploader 에서 예외 발생 시 StorageException 이 발생한다")
    @Test
    void uploadImage_throwsImageUploadException() {
      // given
      MockMultipartFile file = createMockMultipartFile();
      StorageException exception = new StorageException(StorageErrorCode.UPLOAD_FAILED);
      given(imageUploader.upload(any())).willThrow(exception);

      // when & then
      assertThatThrownBy(() -> s3Service.uploadAndGetPublicUrl(file))
              .isInstanceOf(StorageException.class);
    }
  }

  // -- 헬퍼 메서드 --

  private static MockMultipartFile createMockMultipartFile() {
    return new MockMultipartFile(
            FIELD_NAME, StorageServiceTest.FILE_NAME, StorageServiceTest.CONTENT_TYPE, FILE_CONTENT
    );
  }
}
