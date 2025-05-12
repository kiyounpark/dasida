package com.bonju.review.s3;

import com.bonju.review.s3.dto.ImageResponseDto;
import com.bonju.review.s3.exception.ImageUploadErrorCode;
import com.bonju.review.s3.exception.ImageUploadException;
import com.bonju.review.s3.infra.ObjectKey;
import com.bonju.review.s3.infra.ImageProvider;
import com.bonju.review.s3.infra.ImageUploader;
import com.bonju.review.s3.service.S3ServiceImpl1;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

  private static final String FIELD_NAME = "image";
  private static final String FILE_NAME  = "profile.png";
  private static final String CONTENT_TYPE = "image/png";
  private static final byte[] FILE_CONTENT = "mock image content".getBytes();
  private static final String OBJECT_KEY = "images/profile_123456.png";
  private static final String PUBLIC_URL = "https://your-bucket.s3.amazonaws.com/" + OBJECT_KEY;
  @Mock
  ImageUploader imageUploader;

  @Mock
  ImageProvider imageProvider;

  @InjectMocks
  S3ServiceImpl1 s3Service;

  @DisplayName("S3에 이미지를 등록하고 가져온다.")
  @Test
  void uploadImage_returnsPublicUrl() {
    //given
    MockMultipartFile file = createMockMultipartFile();
    ObjectKey objectKey = new ObjectKey(OBJECT_KEY);
    String publicUrl = PUBLIC_URL;

    given(imageUploader.upload(file)).willReturn(objectKey);
    given(imageProvider.getPublicUrl(objectKey)).willReturn(publicUrl);

    //when
    ImageResponseDto imageResponseDto = s3Service.generatePublicUrl(file);

    //then
    assertThat(imageResponseDto.imageUrl()).isEqualTo(publicUrl);
  }

  @DisplayName("이미지 생성 및 public Url 반환중 ImageUploadException 발생")
  @Test
  void xx() {
    //given
    MockMultipartFile file = createMockMultipartFile();
    ImageUploadException exception = new ImageUploadException(ImageUploadErrorCode.UPLOAD_FAILED);
    given(imageUploader.upload(any())).willThrow(exception);

    //then
    assertThatThrownBy(() -> s3Service.generatePublicUrl(file))
            .isInstanceOf(ImageUploadException.class);
  }

  // -- 헬퍼 메서드 --

  private static MockMultipartFile createMockMultipartFile() {
    return new MockMultipartFile(
            FIELD_NAME, FILE_NAME, CONTENT_TYPE, FILE_CONTENT
    );
  }
}
