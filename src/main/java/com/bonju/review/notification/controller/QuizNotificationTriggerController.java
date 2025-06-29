package com.bonju.review.notification.controller;

import com.bonju.review.notification.scheduler.QuizNotificationScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ▸ POST /quiz-notifications/trigger
 *   : 매일 07:30 자동 실행되는 스케줄러를 수동으로 한번 실행해 보는 개발용 엔드포인트
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz-notifications")
@Profile("dev")
public class QuizNotificationTriggerController {

  private final QuizNotificationScheduler scheduler;

  /** 200 OK(본문 없음) */
  @PostMapping("/trigger")
  public ResponseEntity<Void> triggerPushManually() {
    scheduler.pushTodayQuizNotifications();
    return ResponseEntity.ok().build();
  }
}