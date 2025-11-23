package com.bonju.review.dev.domain;

import com.bonju.review.dev.dto.TestProfileResponse;
import java.time.LocalDateTime;

public record TestProfileUser(
    String id,
    String nickname,
    String email,
    String profileImageUrl,
    String status
) {

  public TestProfileResponse toResponse(LocalDateTime createdAt) {
    return new TestProfileResponse(id, nickname, email, profileImageUrl, status, createdAt);
  }
}
