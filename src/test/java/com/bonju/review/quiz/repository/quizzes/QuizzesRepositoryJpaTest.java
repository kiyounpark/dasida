package com.bonju.review.quiz.repository.quizzes;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * QuizzesRepositoryJpa – “미풀이 또는 항상 오답” 퀴즈 조회 테스트
 */
@DataJpaTest
@Import(QuizzesRepositoryJpa.class)
class QuizzesRepositoryJpaTest {

  @Autowired TestEntityManager     em;
  @Autowired QuizzesRepositoryJpa  repo;

  /* ------------------------------------------------------------------
   * findUnsolvedOrAlwaysWrongQuizzes()
   * ------------------------------------------------------------------ */
  @Nested
  @DisplayName("findUnsolvedOrAlwaysWrongQuizzes()")
  class FindUnsolvedOrAlwaysWrong {

    @Test
    @DisplayName("정답 이력이 전혀 없거나, 오답만 기록된 퀴즈만 반환한다")
    void returns_only_unanswered_or_all_wrong() {
      User user = newUser();

      // --- 3일 전 퀴즈 3개 --------------------------------------------------
      Quiz quizNoAnswer   = newQuiz(user, 3,  9);   // 미풀이   ✅ 포함
      Quiz quizAllWrong   = newQuiz(user, 3, 10);   // 오답만  ✅ 포함
      Quiz quizAnsweredOk = newQuiz(user, 3, 11);   // 정답    ❌ 제외

      // 오답 기록
      answer(user, quizAllWrong, false, 3);
      // 정답 기록
      answer(user, quizAnsweredOk, true, 3);

      flushAndClear();

      List<Quiz> result = repo.findUnsolvedOrAlwaysWrongQuizzes(user, 3);

      assertThat(result)
              .extracting(Quiz::getId)
              .containsExactlyInAnyOrder(
                      quizNoAnswer.getId(),
                      quizAllWrong.getId()
              );
    }

    @Test
    @DisplayName("createdAt DESC 로 정렬된다 (가장 최근 항목이 리스트 맨 앞)")
    void orders_by_createdAt_desc() {
      User user = newUser();

      // 02:00 에 생성된 퀴즈 (older)
      newQuiz(user, 3, 2);
      // 08:00 에 생성된 퀴즈 (newer)
      Quiz later   = newQuiz(user, 3, 8);

      flushAndClear();

      List<Quiz> result = repo.findUnsolvedOrAlwaysWrongQuizzes(user, 3);

      // 1) ID 기준으로 "later" 가 맨 앞에 오는지 확인
      assertThat(result)
              .hasSize(2)
              .extracting(Quiz::getId)
              .first()
              .isEqualTo(later.getId());

      // 2) 순서가 createdAt DESC 인 것도 확인하고 싶으면
      assertThat(result.get(0).getCreatedAt())
              .isAfter(result.get(1).getCreatedAt());
    }

  /* ------------------------------------------------------------------
   * helper
   * ------------------------------------------------------------------ */
  private User newUser() {
    User u = User.builder().build();
    em.persist(u);
    return u;
  }

  /** daysAgo 일 전 00:00 + hourOffset 시에 생성된 Quiz */
  private Quiz newQuiz(User owner, int daysAgo, int hourOffset) {
    Quiz q = Quiz.builder()
            .user(owner)
            .knowledge(null)
            .question("Q")
            .answer("A")
            .hint("H")
            .build();
    em.persist(q);

    LocalDateTime created =
            LocalDate.now().minusDays(daysAgo).atStartOfDay().plusHours(hourOffset);
    ReflectionTestUtils.setField(q, "createdAt", created);
    return q;
  }

  /** answerTime 기준으로 UserAnswer 삽입 */
  private void answer(User user, Quiz quiz, boolean correct, int daysAgo) {
    UserAnswer ua = UserAnswer.builder()
            .user(user)
            .quiz(quiz)
            .answer("A")
            .dayType(daysAgo)
            .isCorrect(correct)
            .build();
    em.persist(ua);

    LocalDateTime ts =
            LocalDate.now().minusDays(daysAgo).atStartOfDay().plusHours(12);
    ReflectionTestUtils.setField(ua, "createdAt", ts);
  }

  private void flushAndClear() {
    em.flush();
    em.clear();
  }
}
}