package com.bonju.review.dto;

import jakarta.validation.constraints.NotBlank;

public class KnowledgeTextRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String description;
}
