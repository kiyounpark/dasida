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

  /** quizId를 data 로 포함해서 전송 */
  public String pushQuiz(String token, String title, String body, Long quizId) {
    Message msg = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
            .putData("quizId", quizId.toString())      // 👈 data 채워 넣기
            .build();
    try {
      return firebaseMessaging.send(msg);
    } catch (Exception e) {
      throw new FcmException(FcmErrorCode.PUSH_FAILED, e);
    }
  }
}
