package com.bonju.review.util;

import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;


/**
 * 이미지 URL로부터 적절한 MimeType을 추출하는 유틸 클래스입니다.
 */
public class ContentTypeExtractor {

  private ContentTypeExtractor() {}

  // 파일 확장자 상수
  private static final String JPG_EXTENSION  = ".jpg";
  private static final String JPEG_EXTENSION = ".jpeg";
  private static final String PNG_EXTENSION  = ".png";
  private static final String WEBP_EXTENSION = ".webp";

  // MIME 타입 상수
  private static final MimeType MIME_JPEG = MimeTypeUtils.IMAGE_JPEG;
  private static final MimeType MIME_PNG  = MimeTypeUtils.IMAGE_PNG;
  private static final MimeType MIME_WEBP = MimeType.valueOf("image/webp");

  /**
   * 주어진 이미지 URL의 파일 확장자를 기반으로 MimeType을 결정하여 반환합니다.
   * 지원 포맷: JPG/JPEG, PNG, WEBP
   *
   * @param src 이미지 URL
   * @return 해당 이미지의 MimeType
   * @throws IllegalArgumentException 지원하지 않는 확장자가 포함된 경우
   */
  public static MimeType extractMimeType(String src) {
    // 파일명에서 확장자 추출 후 소문자 표준화
    String extension = FileExtensionExtractor.extract(src).toLowerCase();

    if (JPG_EXTENSION.equals(extension) || JPEG_EXTENSION.equals(extension)) {
      return MIME_JPEG;
    }
    if (PNG_EXTENSION.equals(extension)) {
      return MIME_PNG;
    }
    if (WEBP_EXTENSION.equals(extension)) {
      return MIME_WEBP;
    }
    // 지원하지 않는 확장자는 예외로 처리
    throw new IllegalArgumentException("이미지가 jpg, jpeg, png, webp 이 아닙니다. " + extension);
  }
}
