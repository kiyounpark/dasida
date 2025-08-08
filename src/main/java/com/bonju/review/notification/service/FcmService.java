package com.bonju.review.notification.service;

import com.bonju.review.notification.exception.FcmErrorCode;
import com.bonju.review.notification.exception.FcmException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

  private final FirebaseMessaging firebaseMessaging;
  public String pushToToken(String token, String title, String body) {
    Message msg = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
            .build();
    try {
      return firebaseMessaging.send(msg);
    } catch (Exception e) {
      log.error("[FCM] pushToToken() 실패 → token={}, title={}, body={}", token, title, body, e);
      throw new FcmException(FcmErrorCode.PUSH_FAILED, e);
    }
  }
}
