package com.bonju.review.image.infra;

import com.bonju.review.image.config.S3Properties;
import com.bonju.review.image.exception.exception.StorageException;
import com.bonju.review.image.storage.objectkey.ObjectKey;
import com.bonju.review.image.storage.objectkey.ObjectKeyGenerator;
import com.bonju.review.image.storage.uploader.S3Uploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class S3UploaderTest {

  private static final String FIELD_NAME = "image";
  private static final String FILE_NAME = "profile.png";
  private static final String CONTENT_TYPE = "image/png";
  private static final byte[] FILE_CONTENT = "mock image content".getBytes();
  private static final String OBJECT_KEY = "images/profile_123456.png";

  @Mock
  S3Client s3Client;

  @Mock
  ObjectKeyGenerator objectKeyGenerator;

  @Mock
  S3Properties s3Properties;

  @InjectMocks
  S3Uploader s3Uploader;

  @Nested
  @DisplayName("성공 케이스")
  class SuccessTest {
    @DisplayName("정상적인 MultipartFile 을 업로드하면 ObjectKey를 반환한다")
    @Test
    void uploadsImageToS3AndReturnsObjectKey() {
      //given
      MockMultipartFile mockMultipartFile = createMockMultipartFile();
      given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
              .willReturn(PutObjectResponse.builder().build());
      given(objectKeyGenerator.generate(anyString())).willReturn(OBJECT_KEY);

      //when
      ObjectKey objectKey = s3Uploader.upload(mockMultipartFile);

      //then
      assertThat(objectKey.value()).isEqualTo(OBJECT_KEY);
    }
  }

  @Nested
  @DisplayName("실패 케이스")
  class FailTest{
    @DisplayName("S3Client 에서 S3Exception 이 발생하면 StorageException 으로 감싼다")
    @Test
    void convertsS3ExceptionToImageUploadException(){
      //given
      MockMultipartFile mockMultipartFile = createMockMultipartFile();
      AwsServiceException s3ClientException = S3Exception.builder()
              .message("의도적 예외 발생").build();

      given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
              .willThrow(s3ClientException);
      given(objectKeyGenerator.generate(anyString())).willReturn(OBJECT_KEY);

      //when & then
      assertThatThrownBy(()-> s3Uploader.upload(mockMultipartFile))
              .isInstanceOf(StorageException.class);

    }

    @DisplayName("파일 스트림을 열 수 없으면 ImageUploadException을 던진다")
    @Test
    void throwsImageUploadExceptionWhenGetInputStreamFails() throws IOException {
      // given
      MultipartFile file = mock(MultipartFile.class);

      given(file.getOriginalFilename()).willReturn("fail.jpg");
      given(file.getInputStream()).willThrow(new IOException("stream error"));
      given(objectKeyGenerator.generate(anyString())).willReturn(OBJECT_KEY);

      // when & then
      assertThatThrownBy(() -> s3Uploader.upload(file))
              .isInstanceOf(StorageException.class);
    }
  }

  private static MockMultipartFile createMockMultipartFile() {
    return new MockMultipartFile(
            FIELD_NAME, FILE_NAME, CONTENT_TYPE, FILE_CONTENT
    );
  }
}
