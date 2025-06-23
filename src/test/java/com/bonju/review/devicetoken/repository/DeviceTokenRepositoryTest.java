package com.bonju.review.devicetoken.repository;

import com.bonju.review.devicetoken.entity.DeviceToken;
import com.bonju.review.user.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(DeviceTokenRepositoryJpa.class)   // 수동 등록
class DeviceTokenRepositoryJpaTest {

  private static final String TOKEN = "AAA.BBB.CCC";

  @Autowired EntityManager         em;
  @Autowired DeviceTokenRepository repository;

  /* ------------------------------------------------------------------
   * save() 검증
   * ------------------------------------------------------------------ */
  @Test
  @DisplayName("save() → user·token 이 정상 저장된다")
  @Transactional
  void save_persists_userAndToken() {
    // given
    User user = User.builder().kakaoId("123").nickname("tester").build();
    em.persist(user);

    DeviceToken toSave = DeviceToken.builder()
            .user(user)
            .token(TOKEN)
            .build();

    // when
    DeviceToken saved = repository.save(toSave);

    // then – DB 반영 후 직접 조회해 확인
    em.flush();
    em.clear();
    DeviceToken found = em.find(DeviceToken.class, saved.getId());

    assertThat(found)
            .isNotNull()
            .extracting(DeviceToken::getToken, dt -> dt.getUser().getId(), DeviceToken::isActive)
            .containsExactly(TOKEN, user.getId(), true);
  }

  /* ------------------------------------------------------------------
   * findByUserIdAndToken() 검증
   * ------------------------------------------------------------------ */
  @Test
  @DisplayName("존재하는 (user, token) 쌍이면 Optional 이 채워져 반환된다")
  void findByUserIdAndToken_returnsPresentOptional() {
    // given
    User user = User.builder().kakaoId("321").nickname("lookupTester").build();
    em.persist(user);
    DeviceToken saved = repository.save(DeviceToken.builder().user(user).token(TOKEN).build());
    em.flush();
    em.clear();

    // when
    Optional<DeviceToken> result = repository.findByUserIdAndToken(user, TOKEN);

    // then – Optional 내부 값 확인
    assertThat(result)
            .isPresent()
            .get()
            .extracting(DeviceToken::getId, DeviceToken::getToken)
            .containsExactly(saved.getId(), TOKEN);
  }

  @Test
  @DisplayName("해당 토큰이 없으면 Optional.empty 를 반환한다")
  void findByUserIdAndToken_returnsEmptyOptional() {
    // given
    User user = User.builder().kakaoId("999").nickname("noTokenUser").build();
    em.persist(user);
    em.flush();
    em.clear();

    // when
    Optional<DeviceToken> result = repository.findByUserIdAndToken(user, TOKEN);

    // then
    assertThat(result).isEmpty();
  }

  /* ------------------------------------------------------------------
   * findByUser() 검증 – 새로 추가된 메서드
   * ------------------------------------------------------------------ */
  @Test
  @DisplayName("사용자의 토큰이 하나라도 있으면 Optional 이 채워져 반환된다")
  void findByUser_returnsPresentOptional() {
    // given – 동일 사용자에 두 개 토큰 저장 (setMaxResults(1) 방어 확인)
    User user = User.builder().kakaoId("555").nickname("multiTokenUser").build();
    em.persist(user);
    repository.save(DeviceToken.builder().user(user).token("X.Y.Z").build());
    DeviceToken expected = repository.save(DeviceToken.builder().user(user).token(TOKEN).build());
    em.flush();
    em.clear();

    // when
    Optional<DeviceToken> result = repository.findByUser(user);

    // then – 반환된 토큰 ID가 기대값과 일치하고, 사용자 일치
    assertThat(result)
            .isPresent()
            .get()
            .extracting(DeviceToken::getId, dt -> dt.getUser().getId())
            .containsExactly(expected.getId(), user.getId());
  }

  @Test
  @DisplayName("사용자에게 토큰이 없으면 Optional.empty 반환")
  void findByUser_returnsEmptyOptional() {
    // given
    User user = User.builder().kakaoId("777").nickname("emptyUser").build();
    em.persist(user);
    em.flush();
    em.clear();

    // when
    Optional<DeviceToken> result = repository.findByUser(user);

    // then
    assertThat(result).isEmpty();
  }
}