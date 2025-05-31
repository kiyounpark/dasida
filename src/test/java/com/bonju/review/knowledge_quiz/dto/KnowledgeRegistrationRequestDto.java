package com.bonju.review.knowledge_quiz.dto;

import com.bonju.review.util.annotation.MarkdownLength;
import jakarta.validation.constraints.NotBlank;

public record KnowledgeRegistrationRequestDto(
        @NotBlank String title,
        @NotBlank @MarkdownLength(max = 500) String content
) {}