package com.bonju.review.image.infra;

import com.bonju.review.image.storage.objectkey.ObjectKeyGenerator;
import com.bonju.review.image.storage.objectkey.UuidObjectKeyGenerator;
import com.bonju.review.util.FileExtensionExtractor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

class UuidObjectKeyGeneratorTest {

  private static final String FORM_FIELD_NAME = "file";
  private static final String ORIGINAL_FILENAME = "test-image.png";
  private static final String CONTENT_TYPE = "image/png";
  private static final byte[] FILE_CONTENT = "fake-image-content".getBytes();
  public static final String IMAGE_DIRECTORY_PREFIX = "images/";

  private final ObjectKeyGenerator generator = new UuidObjectKeyGenerator();

  @DisplayName("UUID 기반 ObjectKey 는 'images/'로 시작하고 확장자를 포함한다")
  @Test
  void generate_returnsKeyWithPrefixAndExtension() {
    // given
    MockMultipartFile file = createMockFile();
    String extension = FileExtensionExtractor.extract(file.getOriginalFilename());

    // when
    String objectKey = generator.generate(file.getOriginalFilename());

    // then
    assertThat(objectKey)
            .startsWith(IMAGE_DIRECTORY_PREFIX)
            .endsWith(extension); // ✅ 확장자만 검증
  }

  @DisplayName("ObjectKey 는 유효한 UUID 를 포함한다")
  @Test
  void generate_containsValidUuid() {
    // given
    MockMultipartFile file = createMockFile();
    String extension = FileExtensionExtractor.extract(file.getOriginalFilename());

    // when
    String objectKey = generator.generate(file.getOriginalFilename());

    // UUID 파트 추출
    String uuidPart = objectKey.substring(
            IMAGE_DIRECTORY_PREFIX.length(),
            objectKey.length() - extension.length()
    );

    // then
    assertThatCode(() -> UUID.fromString(uuidPart))
            .doesNotThrowAnyException();
  }

  private static MockMultipartFile createMockFile() {
    return new MockMultipartFile(
            FORM_FIELD_NAME,
            ORIGINAL_FILENAME,
            CONTENT_TYPE,
            FILE_CONTENT
    );
  }
}
