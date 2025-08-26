package com.bonju.review.notification.service;

import com.bonju.review.devicetoken.entity.DeviceToken;
import com.bonju.review.devicetoken.service.DeviceTokenService;
import com.bonju.review.notification.scheduler.QuizNotificationScheduler;
import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.quiz.service.today.QuizTodayService;
import com.bonju.review.user.entity.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FcmServiceUnregisteredTest {

  @Mock FirebaseMessaging firebaseMessaging;
  @Mock QuizTodayService quizTodayService;
  @Mock DeviceTokenService deviceTokenService;

  FcmService fcmService;
  QuizNotificationScheduler scheduler;

  @BeforeEach
  void setUp() {
    fcmService = new FcmService(firebaseMessaging, deviceTokenService);
    scheduler = new QuizNotificationScheduler(fcmService, quizTodayService, deviceTokenService);
  }

  @Test
  void deleteToken_whenMessagingErrorUnregistered_thenSchedulerNextRunNoError() throws Exception {
    String tokenStr = "expired-token";
    User user = User.builder().build();
    ReflectionTestUtils.setField(user, "id", 1L);

    DeviceToken deviceToken = DeviceToken.builder().user(user).token(tokenStr).build();

    Quiz quiz = Quiz.builder().user(user).question("Q").build();
    ReflectionTestUtils.setField(quiz, "id", 1L);

    when(quizTodayService.findTodayQuizList()).thenReturn(List.of(quiz));

    // 연속 호출: 1회차 present, 2회차 empty
    when(deviceTokenService.findOptionalDeviceToken(user))
            .thenReturn(Optional.of(deviceToken))
            .thenReturn(Optional.empty());

    // final + 생성자 접근 불가 → 목으로 대체
    FirebaseMessagingException ex = mock(FirebaseMessagingException.class);
    when(ex.getMessagingErrorCode()).thenReturn(MessagingErrorCode.UNREGISTERED);

    doThrow(ex).when(firebaseMessaging).send(any(Message.class));

    assertDoesNotThrow(() -> scheduler.pushTodayQuizNotifications());
    assertDoesNotThrow(() -> scheduler.pushTodayQuizNotifications());

    verify(deviceTokenService).deleteByToken(tokenStr);
    verify(firebaseMessaging).send(any(Message.class));
  }


}
