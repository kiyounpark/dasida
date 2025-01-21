package com.bonju.review.exception.exception;


public class KakaoUserParseException extends RuntimeException {
    public KakaoUserParseException(String message) {
        super(message);
    }

    public KakaoUserParseException(String message, Throwable cause) {
        super(message, cause);
    }
}

