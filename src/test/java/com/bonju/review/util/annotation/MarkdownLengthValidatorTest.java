package com.bonju.review.util.annotation;

import com.bonju.review.knowledge.dto.KnowledgeRegisterRequestDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownLengthValidatorTest {

  private final Validator validator;

  MarkdownLengthValidatorTest() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    this.validator = factory.getValidator();
  }

  @Test
  void content_plainLength_over500_shouldFail() {
    String content = """
            테스트 데이터를 생성하여 렌더링 후 평문 길이가 500자를 넘도록 합니다. 테스트 데이터를 생성하여 렌더링 후 평문 길이가 500자를 넘도록 합니다. 테스트 데이터를 생성하여 렌더링 후 평문 길이가 500자를 넘도록 합니다. 테스트 데이터를 생성하여 렌더링 후 평문 길이가 500자를 넘도록 합니다. 테스트 데이터를 생성하여 렌더링 후 평문 길이가 500자를 넘도록 합니다. 테스트 데이터를 생성하여 렌더링 후 평문 길이가 500자를 넘도록 합니다. 테스트 데이터를 생성하여 렌더링 후 평문 길이가 500자를 넘도록 합니다. 테스트 데이터를 생성하여 렌더링 후 평문 길이가 500자를 넘도록 합니다. 테스트 데이터를 생성하여 렌더링 후 평문 길이가 500자를 넘도록 합니다. 테스트 데이터를 생성하여 렌더링 후 평문 길이가 500자를 넘도록 합니다. 테스트 데이터를 생성하여 렌더링 후 평문 길이가 500자를 넘도록 합니다. 테스트 데이터를 생성하여 렌더링 후 평문 길이가 500자를 넘도록 합니다. 테스트 데이터를 생성하여 렌더링 후 평문 길이가 500자를 넘도록 합니다. 테스트 데이터를 생성하여 렌더링 후 평문 길이가 500자를 넘도록 합니다.
        """;

    KnowledgeRegisterRequestDto dto = new KnowledgeRegisterRequestDto("제목", content);

    Set<ConstraintViolation<KnowledgeRegisterRequestDto>> violations =
            validator.validate(dto);

    assertThat(violations)
            .anySatisfy(v ->
                    assertThat(v.getMessage()).isEqualTo("내용은 500자를 초과할 수 없습니다.")
            );
  }
}