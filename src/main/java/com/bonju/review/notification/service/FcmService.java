// FcmService.java
package com.bonju.review.notification.service;

import com.bonju.review.notification.exception.FcmErrorCode;
import com.bonju.review.notification.exception.FcmException;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

  private final FirebaseMessaging firebaseMessaging;

  public String pushToToken(String token, String title, String body) {
    // iOS PWA(WebPush) 친화 설정
    WebpushNotification wn = new WebpushNotification(title, body);
    WebpushConfig webpush = WebpushConfig.builder()
            .setNotification(wn)
            .putHeader("TTL", "300")
            .putHeader("Urgency", "high")
            .build();

    Message msg = Message.builder()
            .setToken(token)
            // 기존 알림도 유지 (Android/Chrome 호환)
            .setNotification(Notification.builder().setTitle(title).setBody(body).build())
            // iOS PWA 안정화용 webpush 블록 추가
            .setWebpushConfig(webpush)
            .build();

    try {
      String messageId = firebaseMessaging.send(msg);
      log.info("[FCM] OK messageId={} tokenHash={}", messageId, token.hashCode());
      return messageId;
    } catch (FirebaseMessagingException e) {
      log.error("[FCM] FAIL code={} msg={} tokenHash={}",
              e.getMessagingErrorCode(), e.getMessage(), token.hashCode(), e);
      throw new FcmException(FcmErrorCode.PUSH_FAILED, e);
    } catch (Exception e) {
      log.error("[FCM] FAIL unexpected tokenHash={}", token.hashCode(), e);
      throw new FcmException(FcmErrorCode.PUSH_FAILED, e);
    }
  }
}
