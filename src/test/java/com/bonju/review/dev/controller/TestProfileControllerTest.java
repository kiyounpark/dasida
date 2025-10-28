package com.bonju.review.dev.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value("test-user-id"))
        .andExpect(jsonPath("$.nickname").value("테스트 사용자"))
        .andExpect(jsonPath("$.email").value("test-user@example.com"))
        .andExpect(jsonPath("$.profileImageUrl").value("https://example.com/test-user.png"))
        .andExpect(jsonPath("$.createdAt").value("2024-01-01T09:00:00"));
  }
}
