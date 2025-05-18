package com.bonju.review.image.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties("s3")
@RequiredArgsConstructor
public class S3Properties {
  private final String bucket;
  private final String region;
  private final String accessKey;
  private final String secretKey;
}
