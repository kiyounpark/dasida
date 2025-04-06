package com.bonju.review;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

class FlexMarkTest extends BaseTest {

  private final Parser parser = Parser.builder().build();
  private final HtmlRenderer renderer = HtmlRenderer.builder().build();

  private String toHtml(String markdown) {
    Node document = parser.parse(markdown);
    return renderer.render(document);
  }

  @Test
  @DisplayName("마크다운을 HTML로 변환한다.")
  void markdownToHtml() {
    String markdown = "# 제목입니다\n\n문단입니다.";
    String html = toHtml(markdown);

    assertThat(html)
            .contains("<h1>제목입니다</h1>")
            .contains("<p>문단입니다.</p>");
  }

  @Test
  @DisplayName("마크다운과 HTML img 태그가 혼합된 입력을 HTML로 변환하면 img 태그가 유지되어야 한다.")
  void markdownWithHtmlImgShouldPreserveImgTag() {
    // given
    String markdown = """
            # 제목입니다

            <img src="https://example.com/inline-image.png" alt="설명 이미지" />
            """;

    // when
    String html = toHtml(markdown);

    // then
    assertThat(html)
            .contains("<img src=\"https://example.com/inline-image.png\"")
            .contains("alt=\"설명 이미지\"");
  }

}
