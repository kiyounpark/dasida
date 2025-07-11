package com.bonju.review.knowledge.dto;


import com.bonju.review.util.annotation.MarkdownLength;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class KnowledgeRegisterRequestDto {

    @NotBlank(message = "지식 제목 값이 비었습니다.")
    private final String title;

    @NotBlank(message = "지식 내용이 비었습니다.")
    @MarkdownLength
    private final String content;
}
