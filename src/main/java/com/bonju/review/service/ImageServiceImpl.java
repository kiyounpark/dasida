package com.bonju.review.service;

import com.bonju.review.dto.ImageRequestDto;
import com.bonju.review.exception.exception.S3UploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{

    private final S3Service s3Service;

    @Override
    public String getImageUrl(ImageRequestDto imageRequestDto) {
        try{
            return s3Service.createImageUrl(imageRequestDto.getImage());
        }
        catch (S3Exception e){
            throw new S3UploadException(e.getMessage(),e);
        }
    }
}
