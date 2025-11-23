package com.bonju.review.dev.service;

import com.bonju.review.dev.domain.TestProfileUser;
import com.bonju.review.dev.dto.TestProfileResponse;
import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class TestProfileResponseProvider {

  private static final TestProfileUser TEST_PROFILE_USER = new TestProfileUser(
      "test-user-id",
      "테스트 사용자",
      "test-user@example.com",
      "https://example.com/test-user.png",
      "ACTIVE"
  );

  private final Clock clock;

  public TestProfileResponseProvider(Clock clock) {
    this.clock = clock;
  }

  public TestProfileResponse provide() {
    var createdAt = LocalDateTime.now(clock);
    return TEST_PROFILE_USER.toResponse(createdAt);
  }
}
