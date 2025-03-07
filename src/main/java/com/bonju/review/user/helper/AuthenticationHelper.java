package com.bonju.review.user.helper;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.server.ResponseStatusException;

public class AuthenticationHelper {

    public static String getKaKaoId() {
        try {
            OAuth2User oAuth2User = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return oAuth2User.getAttributes().get("id").toString();
        } catch (NullPointerException | ClassCastException e) {
            // 인증 정보가 없거나 잘못된 경우 적절한 예외를 던짐
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 인증 정보를 가져올 수 없습니다.", e);
        }
    }
}
