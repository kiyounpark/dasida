package com.bonju.review.util.annotation;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jsoup.Jsoup;

public class MarkdownLengthValidator
        implements ConstraintValidator<MarkdownLength, String> {

  private static final Parser PARSER   = Parser.builder().build();
  private static final HtmlRenderer RENDERER = HtmlRenderer.builder().build();

  private int max;

  @Override
  public void initialize(MarkdownLength anno) {
    this.max = anno.max();
  }
  @Override
  public boolean isValid(String value, ConstraintValidatorContext ctx) {
    // ① Markdown → HTML → ② 태그 제거(Preserve entities & spacing)
    String plain = Jsoup.parse(
            RENDERER.render(PARSER.parse(value))
    ).text().trim();          // ← 공백 제거

    return plain.length() <= max;
  }
}