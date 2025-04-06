package com.bonju.review.knowledge.converter;

import com.bonju.review.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MarkdownConverterTest extends BaseTest {

  @Test
  @DisplayName("빈 마크다운 입력은 빈 문자열을 반환한다.")
  void convertTruncatedParagraph_withEmptyInput_returnsEmpty() {
    String result = markdownConverter.convertTruncatedParagraph("");
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("null 입력은 빈 문자열을 반환한다.")
  void convertTruncatedParagraph_withNullInput_returnsEmpty() {
    String result = markdownConverter.convertTruncatedParagraph(null);
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("마크다운에 단락 태그가 없으면 빈 문자열을 반환한다.")
  void convertTruncatedParagraph_withoutParagraph_returnsEmpty() {
    // 예: 단순 헤더만 있는 경우, <p> 태그가 없으므로 빈 결과 예상
    String markdown = "# Title Only";
    String result = markdownConverter.convertTruncatedParagraph(markdown);
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("단락 텍스트 길이가 DEFAULT_MAX_LENGTH 이하이면 그대로 반환한다.")
  void convertTruncatedParagraph_underMaxLength_returnsFullText() {
    String markdown = "This is a short paragraph.";
    String result = markdownConverter.convertTruncatedParagraph(markdown);
    assertThat(result).isEqualTo("This is a short paragraph.");
  }

  @Test
  @DisplayName("단락 텍스트 길이가 DEFAULT_MAX_LENGTH를 초과하면 잘린 후 '...'을 붙여 반환한다.")
  void convertTruncatedParagraph_overMaxLength_returnsTruncatedText() {
    // 100자보다 긴 문자열 생성 (여기서는 고정된 예시 사용)
    String longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus. Suspendisse lectus tortor, dignissim sit amet, adipiscing nec, ultricies sed, dolor.";
    String result = markdownConverter.convertTruncatedParagraph(longText);
    String expected = longText.substring(0, 100) + "...";
    assertThat(result).isEqualTo(expected);
  }
}