package com.bonju.review.user.helper;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.server.ResponseStatusException;

public class AuthenticationHelper {

    private AuthenticationHelper(){}

    public static String getKaKaoId() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = (context != null ? context.getAuthentication() : null);

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();

        try {
            // 1) OAuth2 로그인 직후 (principal = OAuth2User)
            if (principal instanceof OAuth2User oAuth2User) {
                Object id = oAuth2User.getAttributes().get("id");
                if (id == null) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "OAuth2User에 id 속성이 없습니다.");
                }
                return id.toString();
            }

            // 2) remember-me 또는 폼/기타 (principal = UserDetails)
            if (principal instanceof UserDetails userDetails) {
                return userDetails.getUsername(); // 여기 username을 kakaoId로 저장/사용하고 있으므로 그대로 반환
            }

            // 3) 드물게 principal이 문자열인 경우
            if (principal instanceof String s) {
                if ("anonymousUser".equalsIgnoreCase(s)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "익명 사용자입니다.");
                }
                return s;
            }

            // 4) 그 외 알 수 없는 타입
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "지원하지 않는 Principal 타입: " + principal.getClass().getName()
            );
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 인증 정보를 가져올 수 없습니다.", e);
        }
    }

    public static boolean isAnonymousUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return true;
        }

        // principal이 "anonymousUser" 문자열인 경우도 고려
        Object principal = authentication.getPrincipal();
        return principal instanceof String;
    }
}
