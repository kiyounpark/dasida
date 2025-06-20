package com.bonju.review.devicetoken.service;

import com.bonju.review.devicetoken.entity.DeviceToken;
import com.bonju.review.devicetoken.exception.DeviceTokenErrorCode;
import com.bonju.review.devicetoken.exception.DeviceTokenException;
import com.bonju.review.devicetoken.repository.DeviceTokenRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeviceTokenServiceTest {

  @Mock  UserService userService;
  @Mock  DeviceTokenRepository deviceTokenRepository;

  @InjectMocks
  DeviceTokenService deviceTokenService;

  private static final String TOKEN = "AAA.BBB.CCC";

  /* ───────────────────────── 정상 저장 경로 ───────────────────────── */
  @Test
  @DisplayName("registerDeviceToken() — 토큰을 정상 저장한다")
  void registerDeviceToken_savesToken() {
    // given
    User user = User.builder().kakaoId("123").nickname("tester").build();
    given(userService.findUser()).willReturn(user);
    given(deviceTokenRepository.save(any(DeviceToken.class)))
            .willAnswer(inv -> inv.getArgument(0));

    // when
    deviceTokenService.registerDeviceToken(TOKEN);

    // then
    ArgumentCaptor<DeviceToken> captor = ArgumentCaptor.forClass(DeviceToken.class);
    verify(deviceTokenRepository).save(captor.capture());

    DeviceToken saved = captor.getValue();
    assertThat(saved.getUser()).isSameAs(user);
    assertThat(saved.getToken()).isEqualTo(TOKEN);
  }

  /* ───────────────────────── DB 예외 래핑 경로 ───────────────────────── */
  @Test
  @DisplayName("registerDeviceToken() — DataAccessException → DeviceTokenException(DB_FAIL) 로 변환된다")
  void registerDeviceToken_dbError_throwsDeviceTokenException() {
    // given
    User user = User.builder().build();
    given(userService.findUser()).willReturn(user);
    given(deviceTokenRepository.save(any(DeviceToken.class)))
            .willThrow(new DataAccessResourceFailureException("DB down"));

    // when & then
    assertThatThrownBy(() -> deviceTokenService.registerDeviceToken(TOKEN))
            .isInstanceOf(DeviceTokenException.class)
            .hasFieldOrPropertyWithValue("errorCode", DeviceTokenErrorCode.DB_FAIL);
  }
}