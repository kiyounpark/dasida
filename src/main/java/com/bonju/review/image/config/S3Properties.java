package com.bonju.review.image.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@ConfigurationProperties("s3")
@RequiredArgsConstructor
public class S3Properties {

  @NotBlank private final String bucket;
  @NotBlank private final String region;
  @NotBlank private final String accessKey;
  @NotBlank private final String secretKey;
}