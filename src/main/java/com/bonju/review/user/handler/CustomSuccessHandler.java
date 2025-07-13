package com.bonju.review.user.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private static final String COOKIE_NAME       = "JSESSIONID";
    private static final int    COOKIE_MAX_AGE_SEC = (int) Duration.ofDays(1).toSeconds();

    @Value("${auth.redirect-url}")   // ✅ application.yaml 값 주입
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest  request,
            HttpServletResponse response,
            Authentication      authentication
    ) throws IOException, ServletException {

        String sessionId = request.getSession().getId();

        Cookie sessionCookie = new Cookie(COOKIE_NAME, sessionId);
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setMaxAge(COOKIE_MAX_AGE_SEC);
        sessionCookie.setDomain("dasida.org");
        sessionCookie.setSecure(true);

        response.addCookie(sessionCookie);

        // ✅ 프로퍼티로 주입된 URL로 리다이렉트
        response.sendRedirect(redirectUrl);
    }
}