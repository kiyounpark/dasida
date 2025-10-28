package com.bonju.review.dev.controller;

import com.bonju.review.dev.dto.TestProfileResponse;
import com.bonju.review.dev.service.TestProfileResponseProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
@RequestMapping("/api/test/profile")
public class TestProfileController {

  private final TestProfileResponseProvider testProfileResponseProvider;

  public TestProfileController(TestProfileResponseProvider testProfileResponseProvider) {
    this.testProfileResponseProvider = testProfileResponseProvider;
  }

  @GetMapping
  public TestProfileResponse getTestProfile() {
    return testProfileResponseProvider.provide();
  }
}
