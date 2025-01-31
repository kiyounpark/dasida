package com.bonju.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class KnowledgeImageRequestDto {
    @NotBlank
    private final String title;

    @NotNull
    private final MultipartFile imageFile;
}
