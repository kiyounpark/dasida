package com.bonju.review.exception.handler;

import com.bonju.review.devicetoken.exception.DeviceTokenException;
import com.bonju.review.exception.ErrorResponse;
import com.bonju.review.exception.exception.UnauthorizedException;
import com.bonju.review.slack.SlackErrorMessageFactory;
import com.bonju.review.user.exception.UserException;
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
public class UserExceptionHandler {

    private final SlackErrorMessageFactory slackErrorMessageFactory;

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }


    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleDeviceTokenException(UserException e, HttpServletRequest request) {
        String errorMessage = slackErrorMessageFactory.createErrorMessage(request, e);
        log.error(errorMessage);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(e.getHttpStatus().value())
                .error(e.getHttpStatus().getReasonPhrase())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(e.getHttpStatus().value()).body(errorResponse);
    }

}
