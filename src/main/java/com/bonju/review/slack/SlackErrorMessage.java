package com.bonju.review.slack;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class SlackErrorMessage {
  private final String zipkinBaseUrl;
  private final String serviceName;
  private final String layer;
  private final String method;
  private final String uri;
  private final String userId;
  private final String params;
  private final String exceptionName;
  private final String exceptionMessage;
  private final String timestamp;
  private final String traceId;

  public String toSlackFormat() {
    String traceLink = traceId != null
            ? String.format("<%s/zipkin/traces/%s?serviceName=%s|%s>",
            zipkinBaseUrl, traceId, serviceName, traceId)
            : "(no trace)";

    return String.format("""
           🚨 **[%s]**
           🔗 `%s %s`
           👤 `kakaoId=%s`
           📦 `%s`
           🧨 `%s: %s`
           🔍 trace=%s
           🕒 `%s`
           """,
            layer != null ? layer : "UnknownLayer",
            method, uri,
            userId != null ? userId : "UnknownUser",
            params != null ? params : "(no params)",
            exceptionName, exceptionMessage,
            traceLink,
            timestamp
    );
  }
}