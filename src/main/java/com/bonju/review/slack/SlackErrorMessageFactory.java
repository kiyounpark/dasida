/* ---------- slack/SlackErrorMessageFactory.java ---------- */
package com.bonju.review.slack;

import com.bonju.review.user.helper.AuthenticationHelper;
import com.bonju.review.util.MDCTraceIdProvider;
import com.bonju.review.util.TimestampProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
public class SlackErrorMessageFactory {

  private static final String DEFAULT_PARAMS = "(no params)";
  private static final String UNKNOWN_USER   = "(no user)";
  private static final String UNKNOWN_TRACE  = "(no trace)";
  private static final String UNKNOWN_ORIGIN = "UnknownOrigin";

  private final SlackErrorProperties errorProps;
  private final TimestampProvider    timestampProvider;

  public String createErrorMessage(HttpServletRequest req, Throwable ex) {
    validate(req, ex);

    return SlackErrorMessage.builder()
            .zipkinBaseUrl(errorProps.getZipkinBaseUrl())
            .serviceName(errorProps.getServiceName())
            .layer(resolveOrigin(ex))
            .method(req.getMethod())
            .uri(req.getRequestURI())
            .userId(resolveUserId())
            .params(resolveParams(req))
            .exceptionName(ex.getClass().getSimpleName())
            .exceptionMessage(NestedExceptionUtils.getMostSpecificCause(ex).getMessage())
            .timestamp(timestampProvider.formatNow())
            .traceId(resolveTraceId())
            .build()
            .toSlackFormat();
  }

  /* ------------- private helpers ------------- */

  private void validate(HttpServletRequest req, Throwable ex) {
    requireNonNull(errorProps.getZipkinBaseUrl(), "❗ zipkinBaseUrl 필수");
    requireNonNull(errorProps.getServiceName(),   "❗ serviceName 필수");
    requireNonNull(req.getMethod(),               "❗ HTTP method null 금지");
    requireNonNull(req.getRequestURI(),           "❗ URI null 금지");
    requireNonNull(ex.getMessage(),               "❗ exceptionMessage 필수");
  }

  private String resolveUserId() {
    return AuthenticationHelper.isAnonymousUser()
            ? UNKNOWN_USER
            : AuthenticationHelper.getKaKaoId();
  }

  private String resolveParams(HttpServletRequest req) {
    return req.getQueryString() == null ? DEFAULT_PARAMS : req.getQueryString();
  }

  private String resolveTraceId() {
    String traceId = MDCTraceIdProvider.getTraceId();
    return (traceId == null) ? UNKNOWN_TRACE : traceId;
  }

  private String resolveOrigin(Throwable ex) {
    if (ex.getStackTrace().length == 0) return UNKNOWN_ORIGIN;
    var top = ex.getStackTrace()[0];
    String simpleClass = top.getClassName()
            .substring(top.getClassName().lastIndexOf('.') + 1);
    return simpleClass + "#" + top.getMethodName() + "(" + top.getLineNumber() + ")";
  }
}
