package com.bonju.review.s3.infra;

public interface ObjectKeyGenerator {
  String generate(String originalFilename);
}
