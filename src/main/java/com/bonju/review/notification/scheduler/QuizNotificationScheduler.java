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
  private static final String CRON_EVERY_DAY_07_30 = "0 30 7 * * *";
  public static final String ASIA_SEOUL = "Asia/Seoul";

  private final FcmService fcmService;
  private final QuizTodayService quizTodayService;
  private final DeviceTokenService deviceTokenService;

//  @Scheduled(cron = CRON_EVERY_DAY_07_30, zone = ASIA_SEOUL)
  @Scheduled(cron = "0 0/1 * * * *", zone = ASIA_SEOUL)  // 매 1분마다

  public void pushTodayQuizNotifications() {
    try {
      List<Quiz> todayQuizList = quizTodayService.findTodayQuizList();
      for (Quiz quiz : todayQuizList) {
        deviceTokenService.findOptionalDeviceToken(quiz.getUser())
                .ifPresent(deviceToken ->
                        fcmService.pushToToken(deviceToken.getToken(), NOTIFICATION_TITLE, quiz.getQuestion()));

      }
    } catch (Exception e) {
      log.error("알림 전송 중 오류가 발생하였습니다." + e);
    }
  }
}
