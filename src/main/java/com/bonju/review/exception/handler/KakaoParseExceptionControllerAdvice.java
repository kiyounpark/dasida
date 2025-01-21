package com.bonju.review.exception;

import com.bonju.review.exception.exception.KakaoUserParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class KakaoParseExceptionControllerAdvice {

    @ExceptionHandler(KakaoUserParseException.class)
    public ResponseEntity<String> handleKakaoUserParseException(KakaoUserParseException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카카오 로그인 응답 실패: " + e.getMessage());
    }
}
