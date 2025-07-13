package com.bonju.review.knowledge.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MarkdownConverter 단위 테스트")
class MarkdownConverterTest {

  private static final int DEFAULT_MAX_LENGTH = 100;
  private MarkdownConverter converter;

  @BeforeEach
  void setUp() {
    converter = new MarkdownConverter();
  }

  @Nested
  @DisplayName("convertTruncatedParagraph 메서드")
  class ConvertTruncatedParagraph {

    @Test
    @DisplayName("입력이 null 또는 blank일 경우 빈 문자열을 반환한다")
    void returnsEmptyStringWhenInputIsNullOrBlank() {
      assertThat(converter.convertTruncatedParagraph(null)).isEmpty();
      assertThat(converter.convertTruncatedParagraph("   ")).isEmpty();
    }

    @Test
    @DisplayName("문장이 최대 길이 이내이면 텍스트를 그대로 반환한다")
    void returnsParagraphAsIsWhenWithinLimit() {
      String markdown = "단락 하나만 있는 **마크다운** 입니다.";
      String expected = "단락 하나만 있는 마크다운 입니다.";

      String result = converter.convertTruncatedParagraph(markdown);

      assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("여러 문단이 있을 경우 모든 문단을 공백으로 연결하여 반환한다")
    void concatenatesAllParagraphsWithSpace() {
      String markdown = """
                첫 문단입니다.

                두 번째 문단입니다.

                세 번째 문단입니다.
                """;

      String result = converter.convertTruncatedParagraph(markdown);

      assertThat(result)
              .isEqualTo("첫 문단입니다. 두 번째 문단입니다. 세 번째 문단입니다.");
    }

    @Test
    @DisplayName("문장이 최대 길이를 초과하면 지정 길이로 잘라 말줄임표를 추가한다")
    void truncatesLongParagraphAndAppendsEllipsis() {
      String longSentence = "a".repeat(DEFAULT_MAX_LENGTH + 20);

      String result = converter.convertTruncatedParagraph(longSentence);

      assertThat(result)
              .hasSize(DEFAULT_MAX_LENGTH + 3)
              .endsWith("...");
    }
  }
}