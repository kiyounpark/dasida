package com.bonju.review.dev.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bonju.review.dev.service.TestProfileResponseProvider;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TestProfileController.class)
@ActiveProfiles("test")
class TestProfileControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private Clock clock;

  @TestConfiguration
  static class ControllerTestConfiguration {

    @Bean
    TestProfileResponseProvider testProfileResponseProvider(Clock clock) {
      return new TestProfileResponseProvider(clock);
    }
  }

  @Test
  @DisplayName("테스트 프로필 조회 API는 고정된 사용자 정보를 반환한다.")
  void getTestProfileReturnsFixedUserInformation() throws Exception {
    // Given
    var zoneId = ZoneId.of("Asia/Seoul");
    var expectedCreatedAt = LocalDateTime.of(2024, 1, 1, 9, 0, 0);
    var fixedInstant = expectedCreatedAt.atZone(zoneId).toInstant();
    when(clock.instant()).thenReturn(fixedInstant);
    when(clock.getZone()).thenReturn(zoneId);

    // When
    var resultActions = mockMvc.perform(get("/api/test/profile"));

    // Then
    resultActions.andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    var expectedResponseFields = createExpectedResponseFields();
    for (var entry : expectedResponseFields.entrySet()) {
      resultActions.andExpect(jsonPath(entry.getKey()).value(entry.getValue()));
    }
  }

  private Map<String, String> createExpectedResponseFields() {
    var expectedResponseFields = new LinkedHashMap<String, String>();
    expectedResponseFields.put("$.id", "test-user-id");
    expectedResponseFields.put("$.nickname", "테스트 사용자");
    expectedResponseFields.put("$.email", "test-user@example.com");
    expectedResponseFields.put("$.profileImageUrl", "https://example.com/test-user.png");
    expectedResponseFields.put("$.status", "ACTIVE");
    expectedResponseFields.put("$.createdAt", "2024-01-01T09:00:00");
    return expectedResponseFields;
  }
}
