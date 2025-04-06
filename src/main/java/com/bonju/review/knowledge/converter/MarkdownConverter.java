package com.bonju.review.knowledge.converter;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class MarkdownConverter {

  private static final int DEFAULT_MAX_LENGTH = 100;

  private final Parser parser = Parser.builder().build();
  private final HtmlRenderer renderer = HtmlRenderer.builder().build();

  public String convertTruncatedParagraph(String markdown) {
    if (isNullOrBlank(markdown)) return "";

    String html = renderer.render(parser.parse(markdown)).trim();
    if (isNullOrBlank(html)) return "";

    String paragraph = extractFirstParagraph(html);
    if (isNullOrBlank(paragraph)) return "";

    return paragraph.length() <= DEFAULT_MAX_LENGTH
            ? paragraph
            : truncateWithEllipsis(paragraph);
  }

  private String extractFirstParagraph(String html) {
    Document doc = Jsoup.parse(html);
    Elements paragraphs = doc.select("p");
    return paragraphs.text().trim();
  }

  private String truncateWithEllipsis(String text) {
    return text.substring(0, DEFAULT_MAX_LENGTH) + "...";
  }

  private boolean isNullOrBlank(String text) {
    return text == null || text.isBlank();
  }
}
