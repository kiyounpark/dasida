package com.bonju.review.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Builder
public class KnowledgeRegisterRequestDto {

    @NotBlank(message = "지식 제목 값이 비었습니다.")
    private final String title;

    @NotBlank(message = "지식 내용이 비었습니다.")
    private final String text;

    @Size(max = 3, message = "이미지 URL은 최대 3개까지만 등록할 수 있습니다.")
    private final List<String> images;
}
