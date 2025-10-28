package com.bonju.review.dev.service;

import com.bonju.review.dev.dto.TestProfileResponse;
import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class TestProfileResponseProvider {

  private static final String TEST_USER_ID = "test-user-id";
  private static final String TEST_USER_NICKNAME = "테스트 사용자";
  private static final String TEST_USER_EMAIL = "test-user@example.com";
  private static final String TEST_USER_PROFILE_IMAGE_URL = "https://example.com/test-user.png";

  private final Clock clock;

  public TestProfileResponseProvider(Clock clock) {
    this.clock = clock;
  }

  public TestProfileResponse provide() {
    var createdAt = LocalDateTime.now(clock);
    return new TestProfileResponse(
        TEST_USER_ID,
        TEST_USER_NICKNAME,
        TEST_USER_EMAIL,
        TEST_USER_PROFILE_IMAGE_URL,
        createdAt
    );
  }
}
