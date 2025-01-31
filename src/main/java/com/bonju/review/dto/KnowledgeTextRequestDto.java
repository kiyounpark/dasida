package com.bonju.review.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KnowledgeTextRequestDto {

    @NotBlank
    private final String title;

    @NotBlank
    private final String description;
}
