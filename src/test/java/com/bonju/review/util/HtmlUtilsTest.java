package com.bonju.review.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HtmlUtils.extractImageSrcList 테스트")
class HtmlUtilsTest {

  @Test
  @DisplayName("Markdown과 섞여 있어도 여러 img 태그에서 src를 모두 추출한다")
  void extractsMultipleSrcList() {
    String content = """
            ## This is a title
            Some introductory **markdown** text.

            <img src="https://example.com/a.jpg" alt="A">
            - 리스트 아이템 1
            - 리스트 아이템 2

            그리고 또 다른 이미지:
            <img src='https://example.com/b.png' class="thumb">

            ```java
            // 코드 블럭 안의 <img src=\"ignored.jpg\"> 은 무시돼야 합니다.
            System.out.println("<img src=\\\"ignored.jpg\\\">");
            ```

            마무리 이미지:
            <img src="https://example.com/c.webp"/>
            """;

    List<String> srcList = HtmlUtils.extractImageSrcList(content);

    assertThat(srcList)
            .containsExactly(
                    "https://example.com/a.jpg",
                    "https://example.com/b.png",
                    "https://example.com/c.webp"
            );
  }

  @Test
  @DisplayName("src 속성이 없는 img 태그는 무시된다")
  void ignoresNoSrc() {
    String content = """
            <img alt="no-src">
            <img src="">
            <img data-src="ignored.jpg">
            """;

    List<String> srcList = HtmlUtils.extractImageSrcList(content);

    assertThat(srcList).isEmpty();
  }

  @Test
  @DisplayName("HTML에 img 태그가 없으면 빈 리스트를 반환한다")
  void returnsEmptyForNoImgTags() {
    String content = "<p>No images here!</p>";

    List<String> srcList = HtmlUtils.extractImageSrcList(content);

    assertThat(srcList).isEmpty();
  }

  @Test
  @DisplayName("중복된 img 태그의 src는 한 번만 반환된다")
  void removesDuplicateSrcList() {
    String content = """
            <p>Duplicate images:</p>
            <img src="https://example.com/a.jpg">
            <img src="https://example.com/b.png">
            <img src="https://example.com/a.jpg">  <!-- duplicate -->
            <img src="https://example.com/b.png">  <!-- duplicate -->
            <img src="https://example.com/c.webp">
            """;

    List<String> srcList = HtmlUtils.extractImageSrcList(content);

    assertThat(srcList)
            .containsExactly(
                    "https://example.com/a.jpg",
                    "https://example.com/b.png",
                    "https://example.com/c.webp"
            );
  }
}
