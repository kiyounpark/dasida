package com.bonju.review.notification.scheduler;

import com.bonju.review.devicetoken.entity.DeviceToken;
import com.bonju.review.devicetoken.service.DeviceTokenService;
import com.bonju.review.notification.service.FcmService;
import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.quiz.service.today.QuizTodayService;
import com.bonju.review.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuizNotificationScheduler {

  public static final String NOTIFICATION_TITLE = "다시다";
  //  private static final String CRON_EVERY_DAY_07_30 = "0 30 7 * * *";
  private static final String CRON_EVERY_MINUTE = "0 * * * * *";
  public static final String ASIA_SEOUL = "Asia/Seoul";

  private final FcmService fcmService;
  private final QuizTodayService quizTodayService;
  private final DeviceTokenService deviceTokenService;

  //  @Scheduled(cron = CRON_EVERY_DAY_07_30, zone = ASIA_SEOUL)
  @Scheduled(cron = CRON_EVERY_MINUTE, zone = ASIA_SEOUL)
  public void pushTodayQuizNotifications() {
    log.info("[Scheduler] START pushTodayQuizNotifications()");

    try {
      List<Quiz> todayQuizList = quizTodayService.findTodayQuizList();
      log.info("[Scheduler] 대상 퀴즈 수 = {}", todayQuizList.size());

      for (Quiz quiz : todayQuizList) {
        User user = quiz.getUser();
        Long quizId = quiz.getId();

        // 퀴즈 정보 로그
        log.debug("→ quizId={} userId={} question=\"{}\"",
                quizId, user.getId(), quiz.getQuestion());

        // 디바이스 토큰 조회
        Optional<DeviceToken> optToken = deviceTokenService.findOptionalDeviceToken(user);

        if (optToken.isPresent()) {
          DeviceToken token = optToken.get();
          fcmService.pushToToken(token.getToken(), NOTIFICATION_TITLE, quiz.getQuestion());
          log.info("  ✔ PUSH OK  quizId={} userId={}", quizId, user.getId());
        } else {
          log.info("  ✘ TOKEN X  quizId={} userId={}", quizId, user.getId());
        }
      }
    } catch (Exception e) {
      log.error("[Scheduler] 알림 전송 중 오류", e);   // 스택트레이스 포함
    }

    log.info("[Scheduler] END   pushTodayQuizNotifications()");
  }
}
