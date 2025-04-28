package com.bonju.review.slack;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SlackTestController {

  @GetMapping("/slack-test")
  public String testSlack() {
    throw new SlackTestException("🔥 슬랙 테스트용 RuntimeException 발생!");
  }
}
