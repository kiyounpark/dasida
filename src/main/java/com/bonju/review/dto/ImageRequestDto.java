package com.bonju.review.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class ImageRequestDto {
    @NotNull
    private final MultipartFile image;
}
