package com.bonju.review.notification.integration;

import com.bonju.review.notification.service.FcmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 단발성 FCM 푸시 테스트
 * ⚠️ 이 테스트는 실제 디바이스 토큰으로 한 번만 실행하세요.
 */
@SpringBootTest
class FcmOneTimeIntegrationTest {

  @Autowired
  FcmService fcmService;

  // application-test.yaml 에 미리 넣어두세요
  // test:
  //   fcm:
  //     token: "여기에_실제_디바이스_토큰"


  @Test
//  @Disabled("한 번만 실행 후 Disabled 풀어주세요")
  @DisplayName("FCM 단발 실행: 디바이스 토큰으로 실제 알림 보내보기")
  void sendOnePush() {

    String messageId = fcmService.pushToToken(
            "eF-sfNXh-Pd-5EFauXqtJX:APA91bEjItKOVkg8dZuEUAY7ORbC--p5yeK1N2XXw391VdTQM3GUf2TGy23K0Ez-s3EAxs87kUGQeChrdi7FMg6V5Qf1rWjrqr1yvw1yNrojAiNYpGfaxTs",
            "테스트 알림",
            "한 번만 실행하는 푸시 테스트입니다"
    );
    System.out.println("FCM messageId: " + messageId);
  }
}