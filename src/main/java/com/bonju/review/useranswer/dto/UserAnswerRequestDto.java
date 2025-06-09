package com.bonju.review.useranswer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class UserAnswerRequestDto {

    @NotBlank(message = "답변을 입력해주세요.")
    private String answer;

    @NotNull(message = "dayType은 필수입니다.")
    private Integer dayType;  // 0, 3, 7, 30 중 하나
}