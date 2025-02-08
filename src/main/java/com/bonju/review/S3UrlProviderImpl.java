package com.bonju.review;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class S3UrlProviderImpl implements S3UrlProvider {
    @Override
    public String createImageUrl(MultipartFile file) {
        return null;
    }
}
