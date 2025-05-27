package com.bonju.review.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.LinkedHashSet;
import java.util.List;

public class HtmlUtils {

  private static final String IMG_SRC_SELECTOR = "img[src]";
  private static final String ATTR_SRC = "src";
  private static final String CODE_BLOCK_PATTERN = "(?s)```.*?```";

  private HtmlUtils() {}

  /**
   * Markdown/HTML 혼합 컨텐츠에서 HTML <img> 태그의 src 속성만 골라 리스트로 반환한다.
   * 코드 블록 안의 <img> 태그는 무시합니다.
   *
   * @param content the Markdown/HTML fragment
   * @return <img> 태그의 src 속성 값들의 리스트 (중복 제거, 순서 유지)
   */
  public static List<String> extractImageSrcList(String content) {
    // 1) 코드 블록 제거 (``` ... ```)
    String sanitized = content.replaceAll(CODE_BLOCK_PATTERN, "");

    // 2) HTML 파싱 및 <img> 태그 추출
    Document doc = Jsoup.parse(sanitized);
    Elements imgs = doc.select(IMG_SRC_SELECTOR);

    // 3) src 속성 값 수집 및 중복 제거
    LinkedHashSet<String> srcSet = new LinkedHashSet<>();
    imgs.stream()
            .map(el -> el.attr(ATTR_SRC).trim())
            .filter(src -> !src.isEmpty())
            .forEach(srcSet::add);

    // 4) 순서 유지한 불변 리스트 반환
    return List.copyOf(srcSet);
  }
}
