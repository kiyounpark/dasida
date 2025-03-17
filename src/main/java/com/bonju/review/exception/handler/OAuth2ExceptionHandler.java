package com.bonju.review.exception.handler;

import com.bonju.review.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class OAuth2ExceptionHandler {

    @ExceptionHandler(OAuth2AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleOAuth2AuthenticationException(OAuth2AuthenticationException ex, HttpServletRequest request) {
        return buildResponse("소셜 로그인 인증에 실패했습니다. 다시 시도해주세요.", request);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientAuthentication(InsufficientAuthenticationException ex, HttpServletRequest request) {
        return buildResponse("로그인이 필요합니다.", request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(String message, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
