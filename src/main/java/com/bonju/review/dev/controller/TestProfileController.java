package com.bonju.review.dev.controller;

import com.bonju.review.dev.dto.TestProfileResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
@RequestMapping("/api/test/profile")
public class TestProfileController {

  private static final TestProfileResponse TEST_PROFILE_RESPONSE = new TestProfileResponse(
      "test-user-id",
      "테스트 사용자",
      "test-user@example.com",
      "https://example.com/test-user.png"
  );

  @GetMapping
  public TestProfileResponse getTestProfile() {
    return TEST_PROFILE_RESPONSE;
  }
}
