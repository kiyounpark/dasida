package com.bonju.review.image;

import com.bonju.review.image.dto.ImageResponseDto;
import com.bonju.review.image.exception.errorcode.ImageErrorCode;
import com.bonju.review.image.exception.exception.ImageException;
import com.bonju.review.image.exception.exception.StorageException;
import com.bonju.review.image.service.ImageServiceImpl;
import com.bonju.review.image.storage.service.StorageService;
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
class ImageServiceTest {

  private static final String FIELD_NAME = "image";
  private static final byte[] FILE_CONTENT = "mock image content".getBytes();
  private static final String OBJECT_KEY = "images/profile_123456.png";
  private static final String PUBLIC_URL = "https://your-bucket.s3.amazonaws.com/" + OBJECT_KEY;

  private static final String FILE_NAME = "profile.png";
  private static final String CONTENT_TYPE = "image/png";

  private static final String INVALID_EXTENSION_FILE_NAME = "invalid-image.gif";
  private static final String INVALID_CONTENT_TYPE = "image/gif";

  @Mock
  StorageService storageService;

  @InjectMocks
  ImageServiceImpl imageService;

  @Nested
  @DisplayName("generatePublicUrl 성공 케이스")
  class Success {

    @DisplayName("storage 에 이미지를 등록하고 publicUrl 을 반환한다")
    @Test
    void uploadImage_returnsPublicUrl() {
      // given
      ImageResponseDto responseDto = new ImageResponseDto(PUBLIC_URL);
      MockMultipartFile file = createMockMultipartFile(FILE_NAME, CONTENT_TYPE);
      given(storageService.uploadAndGetPublicUrl(any())).willReturn(responseDto);
      // when
      ImageResponseDto imageResponseDto = imageService.uploadAndGetPublicUrl(file);

      // then
      assertThat(imageResponseDto.imageUrl()).isEqualTo(PUBLIC_URL);
    }
  }

  @Nested
  @DisplayName("generatePublicUrl 실패 케이스")
  class Failure {

    @DisplayName("ImageUploader 에서 예외 발생 시 ImageUploadException 이 발생한다")
    @Test
    void uploadImage_throwsImageUploadException() {
      // given
      MockMultipartFile file = createMockMultipartFile(FILE_NAME, CONTENT_TYPE);
      given(storageService.uploadAndGetPublicUrl(any())).willThrow(StorageException.class);
      // when & then
      assertThatThrownBy(() -> imageService.uploadAndGetPublicUrl(file))
              .isInstanceOf(StorageException.class);
    }

    @DisplayName("지원하지 않는 확장자(gif) 업로드 시 ImageUploadException 이 발생한다")
    @Test
    void generatePublicUrl_throwsException_whenFileHasUnsupportedExtension() {
      // given
      MockMultipartFile file = createMockMultipartFile(INVALID_EXTENSION_FILE_NAME, INVALID_CONTENT_TYPE);


      // when & then
      assertThatThrownBy(() -> imageService.uploadAndGetPublicUrl(file))
              .isInstanceOf(ImageException.class)
              .hasMessage(ImageErrorCode.INVALID_EXTENSION.getMessage());
    }

    @DisplayName("지원하지 않는 MIME 타입 업로드 시 ImageUploadException 이 발생한다")
    @Test
    void generatePublicUrl_throwsException_whenFileHasUnsupportedMimeType() {
      // given
      MockMultipartFile file = createMockMultipartFile(FILE_NAME, INVALID_CONTENT_TYPE);

      // when & then
      assertThatThrownBy(() -> imageService.uploadAndGetPublicUrl(file))
              .isInstanceOf(ImageException.class)
              .hasMessage(ImageErrorCode.INVALID_MIME_TYPE.getMessage());
    }
  }

  // -- 헬퍼 메서드 --
  private static MockMultipartFile createMockMultipartFile(String fileName, String contentType) {
    return new MockMultipartFile(
            FIELD_NAME, fileName, contentType, FILE_CONTENT
    );
  }
}
