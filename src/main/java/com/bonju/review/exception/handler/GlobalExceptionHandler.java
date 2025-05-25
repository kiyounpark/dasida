package com.bonju.review.exception.handler;

import com.bonju.review.exception.ErrorResponse;
import com.bonju.review.slack.SlackErrorMessageFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final SlackErrorMessageFactory slackErrorMessageFactory;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        Throwable root = getRootCause(ex);

        if (root instanceof SizeLimitExceededException) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .error(HttpStatus.PAYLOAD_TOO_LARGE.getReasonPhrase())
                    .message("업로드 파일이 너무 큽니다.")
                    .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
                    .path(request.getRequestURI())
                    .timestamp(LocalDateTime.now())
                    .build();

            return new ResponseEntity<>(errorResponse, HttpStatus.PAYLOAD_TOO_LARGE);
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("서버에서 오류가 발생하였습니다.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        String errorMessage = slackErrorMessageFactory.createErrorMessage(request, ex);
        log.error(errorMessage);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }
}
