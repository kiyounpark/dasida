package com.bonju.review.knowledge.repository;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.user.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Import(KnowledgeReadRepositoryImpl.class)
class KnowledgeReadRepositoryTest {

  @Autowired
  EntityManager em;

  @Autowired
  KnowledgeReadRepository knowledgeReadRepository;

  private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 5, 10, 0, 0);
  private static final String KNOWLEDGE_TITLE = "지식 제목";
  private static final String KNOWLEDGE_CONTENT = "지식 내용";


  @DisplayName("정상적으로 요청시, 지식을 Option.of(Knowledge)로 반환하는지 확인한다")
  @Test
  void returnsKnowledge_WhenFindByValidUserAndKnowledgeId(){
    //given
    String nickname = "박기윤";
    User mockUser = registerMockUser("1", nickname);
    Knowledge mockKnowledge = registerMockKnowledge(mockUser);

    clearEntityManager();

    //when
    Optional<Knowledge> optionalKnowledge = knowledgeReadRepository.findKnowledge(mockUser, mockKnowledge.getId());

    //then
    assertThat(optionalKnowledge)
            .hasValueSatisfying(knowledge -> {
              assertThat(knowledge.getTitle()).isEqualTo(KNOWLEDGE_TITLE);
              assertThat(knowledge.getContent()).isEqualTo(KNOWLEDGE_CONTENT);
              assertThat(knowledge.getCreatedAt()).isEqualTo(FIXED_TIME);
              assertThat(knowledge.getUser().getNickname()).isEqualTo(nickname);
            });
  }

  @DisplayName("등록되지 않은 사용자로 조회 시 Optional.empty()를 반환한다")
  @Test
  void should_return_empty_when_knowledge_not_found_for_user() {
    // given
    User owner = registerMockUser("1", "작성자");
    User otherUser = registerMockUser("2", "다른사용자");
    Knowledge knowledge = registerMockKnowledge(owner);
    clearEntityManager();

    // when
    Optional<Knowledge> optionalKnowledge = knowledgeReadRepository.findKnowledge(otherUser, knowledge.getId());

    // then
    assertThat(optionalKnowledge).isEmpty();
  }

  @DisplayName("등록되지 않는 지식 조회시 Optional.empty()를 반환한다")
  @Test
  void should_return_empty_when_knowledge_not_found_by_id(){
    //given
    User mockUser = registerMockUser("1", "박기윤");
    clearEntityManager();

    //when
    Optional<Knowledge> optionalKnowledge = knowledgeReadRepository.findKnowledge(mockUser, 1L);

    //then
    assertThat(optionalKnowledge).isEmpty();
  }

  @DisplayName("지식을 하나도 등록하지 않았다면 hasRegisteredKnowledge는 false를 반환한다")
  @Test
  void hasRegisteredKnowledge_returns_false_when_empty() {
    // given
    User mockUser = registerMockUser("1", "박기윤");
    clearEntityManager();

    // when
    boolean actual = knowledgeReadRepository.hasRegisteredKnowledge(mockUser);

    // then
    assertThat(actual).isFalse();
  }

  @DisplayName("지식을 하나라도 등록했다면 hasRegisteredKnowledge는 true를 반환한다")
  @Test
  void hasRegisteredKnowledge_returns_true_when_exists() {
    // given
    User mockUser = registerMockUser("1", "박기윤");
    registerMockKnowledge(mockUser);
    clearEntityManager();

    // when
    boolean actual = knowledgeReadRepository.hasRegisteredKnowledge(mockUser);

    // then
    assertThat(actual).isTrue();
  }


  // ---- 헬퍼 메서드 ----
  private void clearEntityManager() {
    em.flush();
    em.clear();
  }


  private Knowledge registerMockKnowledge(User user) {
    Knowledge knowledge = createMockKnowledge(user);
    em.persist(knowledge);

    return knowledge;
  }

  private Knowledge createMockKnowledge(User user) {
    return Knowledge.builder()
            .user(user)
            .title(KNOWLEDGE_TITLE)
            .content(KNOWLEDGE_CONTENT)
            .createdAt(FIXED_TIME)
            .build();
  }

  private User registerMockUser(String kakaoId, String userNickname) {
    User user = createMockUser(kakaoId, userNickname);
    em.persist(user);
    return user;
  }

  private User createMockUser(String kakaoId, String userNickname) {
    return User.builder()
            .kakaoId(kakaoId)
            .nickname(userNickname)
            .build();
  }
}