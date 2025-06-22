package com.bonju.review.quiz.repository.find;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuizFindRepositoryJPA.class)   // 구현체 직접 주입
class QuizFindRepositoryJpaTest {

  @Autowired TestEntityManager em;
  @Autowired QuizFindRepository quizFindRepository;

  @Test
  @DisplayName("existsQuizByUser(User) – 해당 유저의 퀴즈 존재 여부를 올바르게 반환한다")
  void existsQuizByUser_returnsExpectedBoolean() {
    /* ─── given ─── */
    User userWithQuiz = createAndPersistUser();
    User userWithoutQuiz = createAndPersistUser();

    Quiz quiz = Quiz.builder()
            .user(userWithQuiz)
            .answer("A")
            .build();
    em.persist(quiz);

    em.flush();
    em.clear();

    /* ─── when & then ─── */
    assertThat(quizFindRepository.isQuizListEmptyByUser(userWithQuiz)).isTrue();
    assertThat(quizFindRepository.isQuizListEmptyByUser(userWithoutQuiz)).isFalse();
  }

  /* ---------- 테스트용 헬퍼 ---------- */

  private User createAndPersistUser() {
    User user = User.builder().build();
    em.persist(user);
    return user;
  }
}