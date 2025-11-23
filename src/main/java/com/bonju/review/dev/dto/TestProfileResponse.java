package com.bonju.review.dev.dto;

import java.time.LocalDateTime;

public record TestProfileResponse(
    String id,
    String nickname,
    String email,
    String profileImageUrl,
    String status,
    LocalDateTime createdAt
) {
}
