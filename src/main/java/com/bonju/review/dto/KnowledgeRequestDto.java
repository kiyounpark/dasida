package com.bonju.review.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class KnowledgeRequestDto {

    @NotBlank
    private final String title;

    @NotBlank
    private final String content;
}
