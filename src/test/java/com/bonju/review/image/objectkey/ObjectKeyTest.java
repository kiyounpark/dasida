package com.bonju.review.image.objectkey;

import com.bonju.review.image.storage.objectkey.ObjectKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ObjectKey 값 객체 단위 테스트")
class ObjectKeyTest {

  @Test
  @DisplayName("유효한 키로 생성하면 value()가 입력값과 동일해야 한다")
  void constructor_withValidValue_returnsValue() {
    // given
    String valid = "images/sample.jpg";

    // when
    ObjectKey key = new ObjectKey(valid);

    // then
    assertThat(key.value()).isEqualTo(valid);
  }

  @Test
  @DisplayName("null 입력 시 IllegalArgumentException을 던진다")
  void constructor_withNull_throwsIllegalArgumentException() {
    // when / then
    assertThatThrownBy(() -> new ObjectKey(null))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("공백 문자열 입력 시 IllegalArgumentException을 던진다")
  void constructor_withBlank_throwsIllegalArgumentException() {
    // when / then
    assertThatThrownBy(() -> new ObjectKey("  "))
            .isInstanceOf(IllegalArgumentException.class);
  }
}
