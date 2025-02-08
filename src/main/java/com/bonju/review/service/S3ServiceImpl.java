package com.bonju.review.service;

import com.bonju.review.S3Uploader;
import com.bonju.review.S3UrlProvider;
import com.bonju.review.dto.ImageRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class S3ServiceImpl implements S3Service{

    @Autowired
    S3UrlProvider s3UrlProvider;
    @Autowired
    S3Uploader s3Uploader;

    @Override
    public String getImageUrl(ImageRequestDto imageRequestDto) {
        s3Uploader.uploadImage(imageRequestDto.getImage());
        return s3UrlProvider.createImageUrl(imageRequestDto.getImage());
    }
}
