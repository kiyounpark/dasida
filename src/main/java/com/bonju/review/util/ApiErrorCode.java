package com.bonju.review.util;

import org.springframework.http.HttpStatus;

public interface ApiErrorCode {
  HttpStatus getHttpStatus();
  String getMessage();
}
