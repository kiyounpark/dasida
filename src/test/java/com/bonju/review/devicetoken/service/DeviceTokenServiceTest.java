package com.bonju.review.devicetoken.service;

import com.bonju.review.devicetoken.entity.DeviceToken;
import com.bonju.review.devicetoken.exception.DeviceTokenErrorCode;
import com.bonju.review.devicetoken.exception.DeviceTokenException;
import com.bonju.review.devicetoken.repository.DeviceTokenRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class DeviceTokenServiceTest {

  @Mock  UserService userService;
  @Mock  DeviceTokenRepository deviceTokenRepository;

  @InjectMocks
  DeviceTokenService deviceTokenService;

  private static final String TOKEN  = "AAA.BBB.CCC";

  /* ───────────────────────── 존재하는 토큰 경로 ───────────────────────── */
  @Test
  @DisplayName("존재하는 토큰이면 그대로 반환한다")
  void returnExistingToken() {
    // given
    User user = User.builder().kakaoId("123").nickname("tester").build();
    DeviceToken saved = DeviceToken.builder().user(user).token(TOKEN).build();

    given(userService.findUser()).willReturn(user);
    given(deviceTokenRepository.findByUserIdAndToken(user, TOKEN))
            .willReturn(Optional.of(saved));

    // when
    String result = deviceTokenService.getOrCreateToken(TOKEN);

    // then
    assertThat(result).isEqualTo(TOKEN);
    // repo.save() 가 절대 호출되지 않아야 함
    verify(deviceTokenRepository, never()).save(any());
  }

  /* ───────────────────────── 신규 토큰 생성 경로 ───────────────────────── */
  @Test
  @DisplayName("토큰이 없으면 새로 만들고 반환한다")
  void createAndReturnNewToken() {
    /* ──────── given ──────── */
    User user = User.builder()
            .kakaoId("123")
            .nickname("tester")
            .build();

    // ① 현재 로그인 사용자
    given(userService.findUser()).willReturn(user);

    // ② (user, token) 조합이 DB에 없음
    given(deviceTokenRepository.findByUserIdAndToken(user, TOKEN))
            .willReturn(Optional.empty());

    // ③ save() 호출 시 전달된 엔티티 그대로 반환하도록 스텁
    given(deviceTokenRepository.save(any(DeviceToken.class)))
            .willAnswer(inv -> inv.getArgument(0));

    /* ──────── when ──────── */
    String result = deviceTokenService.getOrCreateToken(TOKEN);

    /* ──────── then ──────── */
    ArgumentCaptor<DeviceToken> captor = ArgumentCaptor.forClass(DeviceToken.class);
    verify(deviceTokenRepository).save(captor.capture());     // save() 가 한번 호출됐는지
    DeviceToken saved = captor.getValue();

    assertThat(result).isEqualTo(TOKEN);          // 서비스 반환값
    assertThat(saved.getUser()).isSameAs(user);   // user 매핑
    assertThat(saved.getToken()).isEqualTo(TOKEN);
    assertThat(saved.isActive()).isTrue();
  }

  /* ───────────────────────── DB 예외 래핑 경로 ───────────────────────── */
  @Test
  @DisplayName("DB 계층 DataAccessException → DeviceTokenException(DB_FAIL)로 변환된다")
  void dbErrorThrowsDeviceTokenException() {
    // given
    User user = User.builder().build();

    given(userService.findUser()).willReturn(user);
    // 레포지토리가 DataAccessException 을 던지도록 스텁
    given(deviceTokenRepository.findByUserIdAndToken(user, TOKEN))
            .willThrow(new DataAccessResourceFailureException("DB down"));

    // when & then
    assertThatThrownBy(() -> deviceTokenService.getOrCreateToken(TOKEN))
            .isInstanceOf(DeviceTokenException.class)
            .hasFieldOrPropertyWithValue("errorCode", DeviceTokenErrorCode.DB_FAIL);
  }
}