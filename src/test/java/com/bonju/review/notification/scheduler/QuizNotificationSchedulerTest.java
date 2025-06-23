// src/test/java/com/bonju/review/notification/scheduler/QuizNotificationSchedulerRealIT.java
package com.bonju.review.notification.scheduler;

import com.bonju.review.devicetoken.entity.DeviceToken;
import com.bonju.review.devicetoken.repository.DeviceTokenRepository;
import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.quiz.repository.QuizRepository;
import com.bonju.review.user.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest          // 모든 실제 스프링 빈 로딩
@Transactional           // 테스트 후 DB 롤백
class QuizNotificationSchedulerTest {

  @Autowired QuizNotificationScheduler scheduler;

  /* ↓ 단순 Persist 용도 */
  @Autowired EntityManager            em;

  /** 단발성 실전송 테스트 */
  @Test
  @DisplayName("pushTodayQuizNotifications() – 실제 디바이스로 푸시 한 번 발송")
  void pushTodayQuizNotifications_realSend() {
    /* ───── 1) 테스트 데이터 삽입 ─────────────────────────────────── */
    User user = User.builder()
            .kakaoId("real-tester")
            .nickname("실통합")
            .build();
    em.persist(user);

    // createdAt 을 3일 전으로 맞춰 “오늘의 퀴즈” 대상이 되게 함
    Quiz quiz = Quiz.builder()
            .user(user)
            .knowledge(null)
            .question("박기윤의 나이는 ___ 이다.")
            .answer("42")
            .hint("힌트")
            .build();
    em.persist(quiz);
    ReflectionTestUtils.setField(
            quiz, "createdAt", LocalDateTime.now().minusDays(3));

    // 디바이스 토큰 ← 여러분이 제공한 실제 토큰
    DeviceToken token = DeviceToken.builder()
            .user(user)
            .token("eF-sfNXh-Pd-5EFauXqtJX:APA91bEjItKOVkg8dZuEUAY7ORbC--p5yeK1N2XXw391VdTQM3GUf2TGy23K0Ez-s3EAxs87kUGQeChrdi7FMg6V5Qf1rWjrqr1yvw1yNrojAiNYpGfaxTs")
            .build();
    em.persist(token);

    em.flush();
    em.clear();

    /* ───── 2) 스케줄러 메서드 수동 호출 ──────────────────────────── */
    scheduler.pushTodayQuizNotifications();

        /* ───── 3) 별도 Assertion 없이 성공적으로 끝나면 OK ─────────────
           - FCM 서버 응답 오류가 나면 메서드 내부에서 예외가 올라오고
             테스트가 실패하게 됨.
           - 실제로 디바이스 알림이 왔는지 사용자가 확인!
         */
  }
}