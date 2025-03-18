package com.bonju.review.user.vo;

public record KakaoUser(
        Long id,
        String connectedAt,
        UserProperties properties
) {
    public record UserProperties(
            String nickname
    ) {}
}
