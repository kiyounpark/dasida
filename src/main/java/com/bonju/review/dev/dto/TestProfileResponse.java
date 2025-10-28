package com.bonju.review.dev.dto;

public record TestProfileResponse(
    String id,
    String nickname,
    String email,
    String profileImageUrl
) {
}
