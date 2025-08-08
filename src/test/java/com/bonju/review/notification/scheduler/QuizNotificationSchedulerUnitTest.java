/**
 * QuizNotificationScheduler 단위 테스트
 *
 * 요구사항
 * ───────────────────────────────────────────────────────────────
 * ① 오늘의 퀴즈 목록을 조회한 뒤, 사용자 디바이스 토큰이 있으면
 *    FCM 알림을 1회 전송한다.
 * ② 토큰이 없으면 알림을 보내지 않는다.
 * ③ 토큰 有/無가 섞여 있을 때는,
 *    토큰이 있는 사용자에게만 각각 1회씩 발송한다.
 *
 * 테스트 규칙
 * ───────────────────────────────────────────────────────────────
 * - 하나의 테스트는 하나의 개념만 검증한다.
 * - LocalDateTime.now() 대신 FIXED_TIME 상수를 사용한다.
 * - 외부 의존(FcmService, QuizTodayService, DeviceTokenService)은
 *   Mockito @Mock 으로 대체해 완전한 독립 실행을 보장한다.
 */
package com.bonju.review.notification.scheduler;

import com.bonju.review.devicetoken.entity.DeviceToken;
import com.bonju.review.devicetoken.service.DeviceTokenService;
import com.bonju.review.notification.service.FcmService;
import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.quiz.service.today.QuizTodayService;
import com.bonju.review.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizNotificationSchedulerUnitTest {

  /* ------------------------------------------------------------------
   * 고정 시각: 테스트의 결정적(Deterministic) 실행을 위해 사용
   * ------------------------------------------------------------------ */
  private static final LocalDateTime FIXED_TIME =
          LocalDateTime.of(2025, 8, 8, 7, 30);

  /* ------------------------------------------------------------------
   * 외부 의존성 – Mockito Stub
   * ------------------------------------------------------------------ */
  @Mock FcmService          fcmService;
  @Mock QuizTodayService    quizTodayService;
  @Mock DeviceTokenService  deviceTokenService;

  /* SUT(System Under Test) */
  @InjectMocks
  QuizNotificationScheduler scheduler;

  /* ==================================================================
   * 1. 디바이스 토큰이 존재할 때 → FCM 발송 1회
   * ================================================================== */
  @Test
  @DisplayName("디바이스 토큰 O → 푸시 발송 1회")
  void pushTodayQuiz_tokenExists() {
    /* given ───────────────────────────────────────────── */
    User user = User.builder().build();
    ReflectionTestUtils.setField(user, "id", 1L);

    Quiz quiz = Quiz.builder()
            .user(user)
            .question("Q")
            .build();
    ReflectionTestUtils.setField(quiz, "id", 1L);
    ReflectionTestUtils.setField(quiz, "createdAt", FIXED_TIME);

    DeviceToken token = DeviceToken.builder()
            .user(user)
            .token("valid-token")
            .build();

    when(quizTodayService.findTodayQuizList()).thenReturn(List.of(quiz));
    when(deviceTokenService.findOptionalDeviceToken(user))
            .thenReturn(Optional.of(token));

    /* when ────────────────────────────────────────────── */
    scheduler.pushTodayQuizNotifications();

    /* then ──────────────────────────────────────────────
     *  - 토큰이 있으므로 pushToToken() 메서드가 정확히 1회 호출되어야 한다.
     */
    verify(fcmService).pushToToken(
            token.getToken(),
            QuizNotificationScheduler.NOTIFICATION_TITLE,
            quiz.getQuestion()
    );
    verifyNoMoreInteractions(fcmService);   // 그 외 호출 없음
  }

  /* ==================================================================
   * 2. 디바이스 토큰이 존재하지 않을 때 → FCM 미발송
   * ================================================================== */
  @Test
  @DisplayName("디바이스 토큰 X → 푸시 미발송")
  void pushTodayQuiz_tokenNotExists() {
    /* given ───────────────────────────────────────────── */
    User user = User.builder().build();
    ReflectionTestUtils.setField(user, "id", 2L);

    Quiz quiz = Quiz.builder()
            .user(user)
            .question("Q2")
            .build();
    ReflectionTestUtils.setField(quiz, "id", 2L);
    ReflectionTestUtils.setField(quiz, "createdAt", FIXED_TIME);

    when(quizTodayService.findTodayQuizList()).thenReturn(List.of(quiz));
    when(deviceTokenService.findOptionalDeviceToken(user))
            .thenReturn(Optional.empty());

    /* when ────────────────────────────────────────────── */
    scheduler.pushTodayQuizNotifications();

    /* then ──────────────────────────────────────────────
     *  - 토큰이 없으므로 FCM 전송 메서드는 호출되지 않아야 한다.
     */
    verifyNoInteractions(fcmService);
  }

  /* ==================================================================
   * 3. 토큰 有/無 혼재 시나리오
   *    → 토큰 있는 사용자에게만 각각 1회씩 발송
   * ================================================================== */
  @Test
  @DisplayName("토큰 有/無 혼재 → 토큰 있는 사용자에게만 발송")
  void pushTodayQuiz_mixedTokenCases() {
    /* given ───────────────────────────────────────────── */
    // (1) 토큰이 있는 사용자
    User userWith = User.builder().build();
    ReflectionTestUtils.setField(userWith, "id", 10L);
    Quiz quizWith = Quiz.builder()
            .user(userWith)
            .question("WITH")
            .build();
    ReflectionTestUtils.setField(quizWith, "id", 10L);
    ReflectionTestUtils.setField(quizWith, "createdAt", FIXED_TIME);

    // (2) 토큰이 없는 사용자
    User userWithout = User.builder().build();
    ReflectionTestUtils.setField(userWithout, "id", 20L);
    Quiz quizWithout = Quiz.builder()
            .user(userWithout)
            .question("WITHOUT")
            .build();
    ReflectionTestUtils.setField(quizWithout, "id", 20L);
    ReflectionTestUtils.setField(quizWithout, "createdAt", FIXED_TIME);

    DeviceToken token = DeviceToken.builder()
            .user(userWith)
            .token("valid-token")
            .build();

    when(quizTodayService.findTodayQuizList())
            .thenReturn(List.of(quizWith, quizWithout));
    when(deviceTokenService.findOptionalDeviceToken(userWith))
            .thenReturn(Optional.of(token));
    when(deviceTokenService.findOptionalDeviceToken(userWithout))
            .thenReturn(Optional.empty());

    /* when ────────────────────────────────────────────── */
    scheduler.pushTodayQuizNotifications();

    /* then ──────────────────────────────────────────────
     *  - userWith: pushToToken() 1회 호출
     *  - userWithout: 호출 없음
     */
    verify(fcmService).pushToToken(
            token.getToken(),
            QuizNotificationScheduler.NOTIFICATION_TITLE,
            quizWith.getQuestion()
    );
    verifyNoMoreInteractions(fcmService);
  }
}
