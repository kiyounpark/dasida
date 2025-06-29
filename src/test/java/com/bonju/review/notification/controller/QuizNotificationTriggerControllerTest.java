package com.bonju.review.notification.controller;

import com.bonju.review.notification.scheduler.QuizNotificationScheduler;
import com.bonju.review.slack.SlackErrorMessageFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizNotificationTriggerController.class)
@ActiveProfiles("dev")
class QuizNotificationTriggerControllerTest {

  @Autowired
  MockMvc mockMvc;

  // Scheduler 는 빈으로 주입 받아야 하므로 MockBean 으로 대체
  @MockitoBean
  QuizNotificationScheduler scheduler;

  @MockitoBean
  SlackErrorMessageFactory slackErrorMessageFactory;



  @Test
  @WithMockUser

  void POST_trigger_returns_200_and_invokes_scheduler() throws Exception {
    // when - then
    mockMvc.perform(post("/quiz-notifications/trigger")
                    .with(csrf()))
            .andExpect(status().isOk());

    // 호출 한 번 일어났는지 검증
    verify(scheduler, times(1)).pushTodayQuizNotifications();
  }
}