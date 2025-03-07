package com.bonju.review.user.dto;

public class KakaoUserInfoDto {
    private final String id;
    private final String nickname;

    public KakaoUserInfoDto(String id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

}
