package com.bonju.review.knowledge.exception;

import com.bonju.review.util.enums.error_code.KnowledgeErrorCode;
import org.springframework.http.HttpStatus;

public class KnowledgeException extends RuntimeException {
  private final KnowledgeErrorCode errorCode;

  public KnowledgeException(KnowledgeErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }

  public KnowledgeException(KnowledgeErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
  public HttpStatus getHttpStatus() {
    return errorCode.getHttpStatus();
  }
}

