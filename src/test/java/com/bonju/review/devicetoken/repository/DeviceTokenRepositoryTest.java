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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(DeviceTokenRepositoryJpa.class)    // 수동 등록
class DeviceTokenRepositoryJpaTest {

  private static final String TOKEN = "AAA.BBB.CCC";

  @Autowired EntityManager em;
  @Autowired DeviceTokenRepository repository;

  @Test
  @DisplayName("save() → user·token 이 정상 저장된다")
  @Transactional
  void save_persists_userAndToken() {
    /* given */
    User user = User.builder()
            .kakaoId("123")
            .nickname("tester")
            .build();
    em.persist(user);

    DeviceToken toSave = DeviceToken.builder()
            .user(user)
            .token(TOKEN)
            .build();

    /* when */
    DeviceToken saved = repository.save(toSave);

    /* then */
    em.flush();   // DB 반영
    em.clear();   // 1차 캐시 비움

    DeviceToken found = em.find(DeviceToken.class, saved.getId());

    assertThat(found)                     // 실제로 저장됐는지
            .isNotNull()
            .extracting(DeviceToken::getToken,
                    dt -> dt.getUser().getId(),
                    DeviceToken::isActive)
            .containsExactly(TOKEN, user.getId(), true);
  }

  /* ──────────────── findByUserIdAndToken() 검증 ──────────────── */

  @Test
  @DisplayName("존재하는 (user, token) 쌍이면 Optional 에 값이 담겨서 반환된다")
  void findByUserIdAndToken_returnsPresentOptional() {
    // given
    User user = User.builder()
            .kakaoId("321")
            .nickname("lookUpTester")
            .build();
    em.persist(user);

    DeviceToken saved = repository.save(
            DeviceToken.builder()
                    .user(user)
                    .token(TOKEN)
                    .build()
    );
    em.flush();
    em.clear();

    // when
    var result = repository.findByUserIdAndToken(user, TOKEN);

    // then
    assertThat(result)
            .isPresent()
            .get()                       // Optional 내부 값
            .extracting(DeviceToken::getId, DeviceToken::getToken)
            .containsExactly(saved.getId(), TOKEN);
  }

  @Test
  @DisplayName("해당 토큰이 없으면 Optional.empty 를 반환한다")
  void findByUserIdAndToken_returnsEmptyOptional() {
    // given
    User user = User.builder()
            .kakaoId("999")
            .nickname("noTokenUser")
            .build();
    em.persist(user);
    em.flush();
    em.clear();

    // when
    var result = repository.findByUserIdAndToken(user, TOKEN);

    // then
    assertThat(result).isEmpty();
  }
}