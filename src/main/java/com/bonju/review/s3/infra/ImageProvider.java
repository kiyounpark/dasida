package com.bonju.review.s3.infra;

public interface ImageProvider {

  String getPublicUrl(ObjectKey objectKey);
}
