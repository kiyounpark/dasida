package com.bonju.review.user.vo;

public record KakaoUser(
        String id,
        String connectedAt,
        UserProperties properties
) {
    public record UserProperties(
            String nickname
    ) {}
}
