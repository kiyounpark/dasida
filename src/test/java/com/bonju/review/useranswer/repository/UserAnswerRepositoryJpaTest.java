package com.bonju.review.useranswer.repository;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.user.entity.User;
import com.bonju.review.useranswer.entity.UserAnswer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(UserAnswerRepositoryJpa.class)
class UserAnswerRepositoryJpaTest {

  @Autowired
  TestEntityManager em;

  @Autowired
  UserAnswerRepository userAnswerRepository;

  @Test
  @DisplayName("findAll(User) - 주어진 사용자에 해당하는 답안만 조회된다")
  void findAllByUser_returnsOnlyThatUsersAnswers() {
    // given: 두 명의 사용자, 하나의 퀴즈, 각 사용자별 답안 1개씩 저장
    User user1 = createUserAndPersist();
    User user2 = createUserAndPersist();

    Knowledge knowledge = createKnowledgeAndPersist(user1);

    Quiz quiz = createQuizAndPersist(user1, knowledge);

    createUserAnswerAndPersist(user1, quiz);
    createUserAnswerAndPersist(user2, quiz);

    em.flush();
    em.clear();

    // when: user1 으로 findAll 호출
    List<UserAnswer> result = userAnswerRepository.findAll(user1);

    // then: user1 의 답안만 조회되어야 한다
    assertThat(result)
            .hasSize(1)
            .allSatisfy(userAnswer -> assertThat(userAnswer.getUser().getId()).isEqualTo(user1.getId()));
  }

  private Quiz createQuizAndPersist(User user, Knowledge knowledge) {
    Quiz quiz = Quiz.builder()
            .user(user)
            .knowledge(knowledge)
            .answer("correct")
            .build();
    em.persist(quiz);
    return quiz;
  }

  private Knowledge createKnowledgeAndPersist(User user) {
    Knowledge knowledge = Knowledge.builder()
            .user(user)
            .build();
    em.persist(knowledge);
    return knowledge;
  }

  private User createUserAndPersist() {
    User user = User.builder().build();
    em.persist(user);
    return user;
  }

  private void createUserAnswerAndPersist(User user, Quiz quiz){
    UserAnswer userAnswer = UserAnswer.builder()
            .user(user)
            .quiz(quiz)
            .answer("a2")
            .dayType(0)
            .isCorrect(true)
            .build();
    em.persist(userAnswer);
  }
}