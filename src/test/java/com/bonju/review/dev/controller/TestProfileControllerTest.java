package com.bonju.review.dev.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TestProfileController.class)
@ActiveProfiles("test")
class TestProfileControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("테스트 프로필 조회 API는 고정된 사용자 정보를 반환한다.")
  void getTestProfileReturnsFixedUserInformation() throws Exception {
    // Given

    // When
    var resultActions = mockMvc.perform(get("/api/test/profile"));

    // Then
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value("test-user-id"))
        .andExpect(jsonPath("$.nickname").value("테스트 사용자"))
        .andExpect(jsonPath("$.email").value("test-user@example.com"))
        .andExpect(jsonPath("$.profileImageUrl").value("https://example.com/test-user.png"));
  }
}
