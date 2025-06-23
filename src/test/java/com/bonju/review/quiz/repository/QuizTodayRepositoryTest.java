package com.bonju.review.quiz.repository;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.user.entity.User;
import com.bonju.review.useranswer.entity.UserAnswer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * QuizTodayRepository – 복습 대상 조회 & 패치 조인 조회 테스트
 */
@DataJpaTest
@Import(QuizTodayRepository.class)
class QuizTodayRepositoryTest {

  /* ------------------------------------------------------------------
   * DI
   * ------------------------------------------------------------------ */
  @Autowired TestEntityManager    entityManager;
  @Autowired QuizTodayRepository  repository;

  /* ------------------------------------------------------------------
   * 시나리오별 테스트 – ID 조회
   * ------------------------------------------------------------------ */
  @Nested
  @DisplayName("findTodayQuizIds() – 복습 대상 & 미풀이 퀴즈 중 사용자별 최근 1개 선택")
  class FindTodayQuizIds {

    @Test
    @DisplayName("기본: 각 사용자마다 최근 미풀이 한 건이 반환된다")
    void returns_latest_unsolved_per_user() {
      User user1 = newUser();
      User user2 = newUser();

      Quiz quiz3dUser1  = newQuiz(user1, 3);   // 선택 대상
      Quiz quiz7dUser1  = newQuiz(user1, 7);   // 오늘 풀이됨 → 제외
      Quiz quiz30dUser2 = newQuiz(user2, 30);  // 선택 대상

      answerToday(user1, quiz7dUser1);
      flushAndClear();

      List<Long> resultIds = repository.findTodayQuizIds();

      assertThat(resultIds)
              .containsExactlyInAnyOrder(quiz3dUser1.getId(), quiz30dUser2.getId());
    }

    @Test
    @DisplayName("대상 일수가 아닌 퀴즈는 무시된다")
    void ignores_quiz_not_in_target_days() {
      User user = newUser();
      Quiz quiz1d = newQuiz(user, 1); // 대상 아님
      Quiz quiz3d = newQuiz(user, 3); // 대상
      flushAndClear();

      List<Long> ids = repository.findTodayQuizIds();

      assertThat(ids)
              .containsExactly(quiz3d.getId())
              .doesNotContain(quiz1d.getId());
    }

    @Test
    @DisplayName("여러 미풀이 후보가 있을 때 최근(createdAt max) 1개만 선택한다")
    void picks_latest_when_multiple_unsolved() {
      User user = newUser();
      newQuiz(user, 30);
      newQuiz(user, 7);
      Quiz quiz3d = newQuiz(user, 3); // 기대값
      flushAndClear();

      List<Long> ids = repository.findTodayQuizIds();

      assertThat(ids).containsExactly(quiz3d.getId());
    }
  }

  /* ------------------------------------------------------------------
   * 시나리오별 테스트 – 패치 조인 조회
   * ------------------------------------------------------------------ */
  @Nested
  @DisplayName("findByIdsWithUser() – Quiz + User 패치 조인")
  class FindByIdsWithUser {

    @Test
    @DisplayName("ID 리스트로 퀴즈와 작성자를 함께 로딩한다")
    void fetch_quiz_with_user_by_ids() {
      User user = newUser();
      Quiz quiz3d = newQuiz(user, 3);
      Quiz quiz7d = newQuiz(user, 7);
      flushAndClear();

      List<Quiz> quizzes = repository.findByIdsWithUser(List.of(quiz3d.getId(), quiz7d.getId()));

      assertThat(quizzes)
              .hasSize(2)
              .allSatisfy(q -> assertThat(q.getUser()).isNotNull());
    }
  }

  /* ------------------------------------------------------------------
   * Helper methods
   * ------------------------------------------------------------------ */
  private User newUser() {
    User user = User.builder().build();
    entityManager.persist(user);
    return user;
  }

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