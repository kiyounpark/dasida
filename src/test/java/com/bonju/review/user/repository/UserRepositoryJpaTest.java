package com.bonju.review.user.repository;

import com.bonju.review.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRepositoryJpa – count() JPQL 검증
 */
@DataJpaTest
@Import(UserRepositoryJpa.class)
class UserRepositoryJpaTest {

  @Autowired TestEntityManager em;
  @Autowired UserRepositoryJpa  repo;

  /* ---------- helpers -------------------------------------------------- */

  private void insertUsers(int n) {
    for (int i = 0; i < n; i++) em.persist(User.builder().build());
    em.flush();
  }

  /* ---------- tests ---------------------------------------------------- */

  @Test
  @DisplayName("사용자가 하나도 없으면 count() 는 0 을 반환한다")
  void count_returns_0_when_no_users() {
    // Given – DB 비어 있음

    // When
    long result = repo.count();

    // Then
    assertThat(result).isZero();
  }

  @Test
  @DisplayName("사용자가 999명일 때 count() 는 999 를 반환한다")
  void count_returns_999_when_999_users_exist() {
    // Given
    insertUsers(999);

    // When
    long result = repo.count();

    // Then
    assertThat(result).isEqualTo(999);
  }

  @Test
  @DisplayName("사용자가 1000명일 때 count() 는 1000 을 반환한다")
  void count_returns_1000_when_1000_users_exist() {
    // Given
    insertUsers(1000);

    // When
    long result = repo.count();

    // Then
    assertThat(result).isEqualTo(1000);
  }
}