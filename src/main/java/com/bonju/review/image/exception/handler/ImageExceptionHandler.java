package com.bonju.review.image.exception.handler;

import com.bonju.review.exception.ErrorResponse;
import com.bonju.review.image.exception.errorcode.ImageErrorCode;
import com.bonju.review.image.exception.exception.ImageException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ImageExceptionHandler {

  @ExceptionHandler(ImageException.class)
  public ResponseEntity<ErrorResponse> handleKnowledgeException(ImageException e, HttpServletRequest request) {

    ErrorResponse errorResponse = ErrorResponse.builder()
            .status(e.getHttpStatus().value())
            .error(e.getHttpStatus().getReasonPhrase())
            .message(e.getMessage())
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();

    HttpStatus status = e.getErrorCode() == ImageErrorCode.FILE_TOO_LARGE
            ? HttpStatus.PAYLOAD_TOO_LARGE   // 413
            : HttpStatus.BAD_REQUEST;

    return ResponseEntity.status(status.value()).body(errorResponse);
  }

}
