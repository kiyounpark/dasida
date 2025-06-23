package com.bonju.review.quiz.repository;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.user.entity.User;
import com.bonju.review.useranswer.entity.UserAnswer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * QuizTodayNativeRepository – 다양한 시나리오 검증 (사용자별 최근 퀴즈 1건 반환)
 */
@DataJpaTest
@Import(QuizTodayRepository.class)
class QuizTodayRepositoryTest {

  /* ------------------------------------------------------------------
   * DI
   * ------------------------------------------------------------------ */
  @Autowired TestEntityManager         entityManager;
  @Autowired
  QuizTodayRepository repository;
  @Autowired Environment               env;

  @BeforeEach
  void logDatasource() {
    System.out.println("▶ datasource = " + env.getProperty("spring.datasource.url"));
  }

  /* ------------------------------------------------------------------
   * 시나리오별 테스트
   * ------------------------------------------------------------------ */
  @Nested
  @DisplayName("findTodayQuizIds() – 복습 대상 & 미풀이 퀴즈 중 사용자별 최근 1개 선택")
  class FindTodayQuizIds {

    @Test
    @DisplayName("기본: 각 사용자마다 최근 미풀이 한 건이 반환된다")
    void returns_latest_unsolved_per_user() {
      // given
      User user1 = newUser();
      User user2 = newUser();

      Quiz quiz3dUser1  = newQuiz(user1, 3);   // 선택 대상
      Quiz quiz7dUser1  = newQuiz(user1, 7);   // 오늘 풀이됨 → 제외
      Quiz quiz30dUser2 = newQuiz(user2, 30);  // 선택 대상

      answerToday(user1, quiz7dUser1);
      flushAndClear();

      // when
      List<Long> resultIds = repository.findTodayQuizIds();

      // then – 한 체인에 기대값 표현
      assertThat(resultIds)
              .containsExactlyInAnyOrder(quiz3dUser1.getId(), quiz30dUser2.getId());
    }

    @Test
    @DisplayName("대상 일수가 아닌 퀴즈는 무시된다")
    void ignores_quiz_not_in_target_days() {
      // given
      User user = newUser();
      Quiz quiz1d = newQuiz(user, 1); // 대상 아님
      Quiz quiz3d = newQuiz(user, 3); // 대상
      flushAndClear();

      // when
      List<Long> ids = repository.findTodayQuizIds();

      // then – 하나의 체인으로 표현
      assertThat(ids)
              .containsExactly(quiz3d.getId())
              .doesNotContain(quiz1d.getId());
    }

    @Test
    @DisplayName("여러 미풀이 후보가 있을 때 최근(createdAt max) 1개만 선택한다")
    void picks_latest_when_multiple_unsolved() {
      // given
      User user = newUser();
      newQuiz(user, 30); // 오래된
      newQuiz(user, 7);  // 중간
      Quiz quiz3d = newQuiz(user, 3); // 가장 최근 – 기대값
      flushAndClear();

      // when
      List<Long> ids = repository.findTodayQuizIds();

      // then
      assertThat(ids).containsExactly(quiz3d.getId());
    }
  }

  /* ------------------------------------------------------------------
   * Helper methods – 한 역할만 수행, 고정 값 사용
   * ------------------------------------------------------------------ */

  private User newUser() {
    User user = User.builder().build();
    entityManager.persist(user);
    return user;
  }

  /**
   * daysAgo 값만 바꿔 다양한 날짜 생성. createdAt 필드는 reflection으로 덮어쓴다.
   */
  private Quiz newQuiz(User owner, int daysAgo) {
    Quiz quiz = Quiz.builder()
            .user(owner)
            .knowledge(null)
            .question("Q")
            .answer("A")
            .hint("H")
            .build();
    entityManager.persist(quiz);
    ReflectionTestUtils.setField(quiz, "createdAt", LocalDateTime.now().minusDays(daysAgo));
    return quiz;
  }

  private void answerToday(User user, Quiz quiz) {
    UserAnswer ua = UserAnswer.builder()
            .user(user)
            .quiz(quiz)
            .answer("A")
            .dayType(0)
            .isCorrect(true)
            .build();
    entityManager.persist(ua);
    ReflectionTestUtils.setField(ua, "createdAt", LocalDateTime.now());
  }

  private void flushAndClear() {
    entityManager.flush();
    entityManager.clear();
  }
}
