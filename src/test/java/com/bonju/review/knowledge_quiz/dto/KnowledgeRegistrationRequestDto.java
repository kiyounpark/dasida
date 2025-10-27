package com.bonju.review.knowledge_quiz.dto;

import jakarta.validation.constraints.NotBlank;

public record KnowledgeRegistrationRequestDto(
        @NotBlank String title,
        @NotBlank String content
) {}