package com.bonju.review.notification.service;

import com.bonju.review.notification.exception.FcmErrorCode;
import com.bonju.review.notification.exception.FcmException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

/**
 * FcmService 단위 테스트 – “행동(호출) + 반환값/예외” 검증
 */
@ExtendWith(MockitoExtension.class)
class FcmServiceTest {

  private static final String TOKEN  = "dEvIcE-ToKeN";
  private static final String TITLE  = "title";
  private static final String BODY   = "body";
  private static final String MSG_ID = "projects/app/messages/123";

  @Mock        FirebaseMessaging firebaseMessaging;
  @InjectMocks FcmService         fcmService;

  /* ─────────── 정상 경로 ─────────── */
  @Test
  @DisplayName("pushToToken – FirebaseMessaging.send 가 호출되고 messageId 를 반환한다")
  void pushToToken_success() throws Exception {
    // given
    given(firebaseMessaging.send(any(Message.class))).willReturn(MSG_ID);

    // when
    String actual = fcmService.pushToToken(TOKEN, TITLE, BODY);

    // then
    assertThat(actual).isEqualTo(MSG_ID);
    verify(firebaseMessaging).send(any(Message.class)); // 호출 여부만 확인
  }

  /* ─────────── 예외 경로 ─────────── */
  @Test
  @DisplayName("pushToToken – FirebaseMessaging 예외 시 FcmException(PUSH_FAILED) 으로 래핑한다")
  void pushToToken_failure() throws Exception {
    // given
    given(firebaseMessaging.send(any(Message.class)))
            .willThrow(new RuntimeException("network down"));

    // when & then
    assertThatThrownBy(() -> fcmService.pushToToken(TOKEN, TITLE, BODY))
            .isInstanceOf(FcmException.class)
            .hasFieldOrPropertyWithValue("errorCode", FcmErrorCode.PUSH_FAILED);

    verify(firebaseMessaging).send(any(Message.class)); // 예외 경로에서도 호출 확인
  }
}