package com.bonju.review.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ContentTypeExtractor.extractMimeType 테스트")
class ContentTypeExtractorTest {

  @Test
  @DisplayName("지원되는 확장자의 URL로부터 올바른 MimeType을 반환한다")
  void returnsCorrectMimeTypes() {
    List<String> urls = List.of(
            "https://example.com/image1.jpg",
            "https://example.com/image2.JPEG",
            "file.PNG",
            "path/to/graphic.webp"
    );

    List<MimeType> mimeTypes = urls.stream()
            .map(ContentTypeExtractor::extractMimeType)
            .toList();

    assertThat(mimeTypes)
            .containsExactly(
                    MimeTypeUtils.IMAGE_JPEG,
                    MimeTypeUtils.IMAGE_JPEG,
                    MimeTypeUtils.IMAGE_PNG,
                    MimeType.valueOf("image/webp")
            );
  }

  @Test
  @DisplayName("지원되지 않는 확장자가 주어지면 IllegalArgumentException을 던진다")
  void throwsOnUnsupportedExtension() {
    List<String> invalidUrls = List.of(
            "image.gif",
            "document.pdf",
            "unknown.xyz"
    );

    for (String url : invalidUrls) {
      assertThatThrownBy(() -> ContentTypeExtractor.extractMimeType(url))
              .isInstanceOf(IllegalArgumentException.class);
    }
  }
}
