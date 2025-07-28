package com.bonju.review.user.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final RememberMeServices rememberMeServices;

    private static final String COOKIE_NAME       = "JSESSIONID";

    @Value("${auth.redirect-url}")   // ✅ application.yaml 값 주입
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest  request,
            HttpServletResponse response,
            Authentication      authentication
    ) throws IOException, ServletException {

        // ✅ 프로퍼티로 주입된 URL로 리다이렉트
        response.sendRedirect(redirectUrl);
    }
}