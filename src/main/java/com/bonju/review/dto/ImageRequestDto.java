package com.bonju.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class ImageRequestDto {
    private final MultipartFile image;
}
