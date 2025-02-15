package com.bonju.review.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();  // 기존 세션 유지
        String sessionId = session.getId();  // 현재 세션 ID 가져오기

        // ✅ JSESSIONID 쿠키 생성 및 설정
        Cookie sessionCookie = new Cookie("JSESSIONID", sessionId);
        sessionCookie.setPath("/");  // 모든 경로에서 접근 가능
        sessionCookie.setHttpOnly(true);  // JavaScript에서 접근 불가능 (보안 강화)
        sessionCookie.setMaxAge((int) Duration.ofDays(1).toSeconds());  // 1일 유지
        sessionCookie.setDomain("dasida.local");  // ✅ 쿠키 도메인 설정 (프론트엔드에서 접근 가능)
        sessionCookie.setSecure(false);  // ✅ HTTPS 사용 시 `true`, 현재는 HTTP 사용 중이므로 `false`

        response.addCookie(sessionCookie);  // 응답에 쿠키 추가

        // ✅ 로그인 성공 후 프론트엔드 홈 페이지로 리디렉트
        response.sendRedirect("http://dasida.local:3000/home");
    }
}
