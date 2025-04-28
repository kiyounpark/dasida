package com.bonju.review.slack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "slack-message")
@Getter
@AllArgsConstructor
public class SlackErrorProperties {

  private String zipkinBaseUrl;
  private String serviceName;
}
