package com.bonju.review.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileExtensionExtractorTest {

  @DisplayName("확장자가 포함된 파일명에서 .확장자를 정상적으로 반환한다.")
  @Test
  void extract_returnsExtension_whenFilenameIsValid(){
    //given
    String extension = ".png";
    String result = FileExtensionExtractor.extract(extension);
    assertThat(result).isEqualTo(extension);
  }
}
