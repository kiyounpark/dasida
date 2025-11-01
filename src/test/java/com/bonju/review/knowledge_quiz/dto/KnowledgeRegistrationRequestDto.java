package com.bonju.review.knowledge_quiz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record KnowledgeRegistrationRequestDto(
        @NotBlank String title,
        @NotBlank String content,
        @Size(max = 3) List<String> images
) {}
