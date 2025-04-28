package com.bonju.review.util;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class MDCTraceIdProvider {

  private static final String TRACE_ID = "traceId";
  public static String getTraceId() {
    return MDC.get(TRACE_ID);
  }

  private MDCTraceIdProvider(){}
}
