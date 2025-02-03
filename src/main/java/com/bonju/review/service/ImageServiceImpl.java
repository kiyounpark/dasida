package com.bonju.review.service;

import com.bonju.review.dto.ImageRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{

    private final S3Service s3Service;

    @Override
    public String getImageUrl(ImageRequestDto imageRequestDto) {
        return s3Service.createImageUrl(imageRequestDto.getImage());
    }
}
