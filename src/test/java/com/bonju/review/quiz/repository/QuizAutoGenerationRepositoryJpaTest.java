package com.bonju.review.quiz.repository;

import com.bonju.review.quiz.entity.Quiz;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuizAutoGenerationRepositoryJpa.class)
@DisplayName("QuizAutoGenerationRepositoryJpa 통합 테스트")
class QuizAutoGenerationRepositoryJpaTest {

  @Autowired
  QuizAutoGenerationRepositoryJpa repository;

  @Autowired
  EntityManager em;

  @Test
  @DisplayName("saveAll 저장 후 총 엔티티 개수만 검증한다")
  void saveAll_persistsCorrectCount() {
    // given
    Quiz q1 = Quiz.builder().question("Q1").answer("A1").hint("H1").build();
    Quiz q2 = Quiz.builder().question("Q2").answer("A2").hint("H2").build();
    repository.saveAll(List.of(q1, q2));

    // flush & clear to simulate new transaction
    em.flush();
    em.clear();

    // when
    List<Quiz> found = fetchAllQuizzes();

    // then: 개수만 확인
    assertThat(found).hasSize(2);
  }

  @Test
  @DisplayName("saveAll 저장 후 단일 엔티티 내용이 올바르게 저장되었는지 검증한다")
  void saveAll_persistsCorrectContentForSingle() {
    // given
    String question = "SingleQ";
    String answer = "SingleA";
    String hint = "SingleH";
    Quiz q = Quiz.builder().question(question).answer(answer).hint(hint).build();
    repository.saveAll(List.of(q));

    em.flush();
    em.clear();

    // when
    List<Quiz> found = fetchAllQuizzes();

    // then: 첫 번째(유일) 엔티티의 내용 확인
    Quiz saved = found.getFirst();
    assertThat(saved.getQuestion()).isEqualTo(question);
    assertThat(saved.getAnswer()).isEqualTo(answer);
    assertThat(saved.getHint()).isEqualTo(hint);
  }



  /**
   * 현재 DB에 저장된 모든 Quiz 엔티티를 조회해 리스트로 반환한다.
   */
  private List<Quiz> fetchAllQuizzes() {
    return em.createQuery("SELECT q FROM Quiz q", Quiz.class)
            .getResultList();
  }
}
