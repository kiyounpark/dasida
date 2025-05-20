package com.bonju.review.image.storage.objectkey;

public record ObjectKey(String value) {
  public ObjectKey {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("ObjectKey 값은 null 이거나 공백일 수 없습니다.");
    }
  }
}
