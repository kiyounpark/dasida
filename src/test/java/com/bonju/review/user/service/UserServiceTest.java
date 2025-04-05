package com.bonju.review.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;



import com.bonju.review.BaseTest;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.vo.KakaoUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

class UserServiceTest extends BaseTest {

  @Test
  @DisplayName("유저가 없을 경우, 새로운 유저를 저장한다")
  @Transactional
  void upsertUser_whenUserNotExist() {
    // given
    String kakaoId = "kakao123";
    Optional<User> foundUser = userRepository.findByKaKaoId(kakaoId);
    assertThat(foundUser)
            .as("테스트 시작 전 해당 유저가 없어야 합니다.")
            .isEmpty();

    KakaoUser kakaoUser = new KakaoUser(
            kakaoId,
            "2025-04-05T10:00:00",
            new KakaoUser.UserProperties("TestNickname")
    );

    // when
    userService.upsertUserByKakaoId(kakaoUser);

    // then
    User savedUser = userRepository.findByKaKaoId(kakaoId)
            .orElseThrow(() -> new AssertionError("유저가 저장되지 않았습니다."));
    assertThat(savedUser.getNickname())
            .as("저장된 유저의 닉네임이 일치해야 합니다.")
            .isEqualTo("TestNickname");
  }


  @Test
  @DisplayName("유저가 존재할 경우, 닉네임을 업데이트 한다")
  @Transactional
  void upsertUser_whenUserExists() {
    // given
    String kakaoId = "kakao123";
    KakaoUser kakaoUser = new KakaoUser(
            kakaoId,
            "2025-04-05T10:00:00",
            new KakaoUser.UserProperties("TestNickname")
    );

    User existingUser = User.builder()
            .kakaoId(kakaoId)
            .nickname("OldNickName")
            .build();

    userRepository.save(existingUser);

    // when
    userService.upsertUserByKakaoId(kakaoUser);

    // then
    assertThat(userRepository.findByKaKaoId(kakaoId))
            .as("저장된 유저가 존재해야 합니다.")
            .isPresent()
            .get()
            .extracting(User::getNickname)
            .as("저장된 유저의 닉네임이 'TestNickname'이어야 합니다.")
            .isEqualTo("TestNickname");
  }
}
