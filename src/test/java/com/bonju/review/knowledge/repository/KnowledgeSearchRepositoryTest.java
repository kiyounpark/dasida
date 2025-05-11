package com.bonju.review.knowledge.repository;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.user.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(KnowledgeSearchRepositoryImpl.class)
class KnowledgeSearchRepositoryTest {

  public static final LocalDateTime FIXED_DATE = LocalDateTime.of(2025, 5, 11, 0, 0);
  public static final String SEARCH_TITLE = "제목";
  public static final String DEFAULT_NICKNAME = "박기윤";
  public static final String DEFAULT_KAKAO_ID = "1";
  public static final String DEFAULT_CONTENT = "내용";
  public static final String UNMATCHED_TITLE_1 = "다른 제목 1";
  public static final String UNMATCHED_TITLE_2 = "완전히 다른 제목 2";
  public static final String UNMATCHED_SEARCH_KEYWORD = "일치하지 않는 검색어";

  @Autowired
  EntityManager em;

  @Autowired
  KnowledgeSearchRepository knowledgeSearchRepository;

  @AfterEach
  void tearDown() { entityManagerClear(); }

  @Nested
  @DisplayName("지식 검색 성공 케이스")
  class SearchSuccessTest {

    @DisplayName("저장된 지식을 찾을 경우, 리스트에 담아 반환한다.")
    @Test
    void returnKnowledgeListContainingTitle_WhenSearchedByUser() {
      // given
      User user = persistUser(DEFAULT_NICKNAME, DEFAULT_KAKAO_ID);
      Knowledge knowledge = persistKnowledge(user, SEARCH_TITLE, FIXED_DATE);

      // when
      List<Knowledge> knowledgeList = knowledgeSearchRepository.findByTitleContaining(user, SEARCH_TITLE);
      Knowledge searchKnowledge = knowledgeList.getFirst();

      // then
      assertThat(searchKnowledge.getCreatedAt()).isEqualTo(knowledge.getCreatedAt());
      assertThat(searchKnowledge.getTitle()).isEqualTo(knowledge.getTitle());
      assertThat(searchKnowledge.getUser().getNickname()).isEqualTo(knowledge.getUser().getNickname());
    }

    @DisplayName("검색된 지식 목록은 createdAt 기준 내림차순으로 정렬된다.")
    @Test
    void returnListSortedByCreatedAtDesc() {
      // given
      User user = persistUser(DEFAULT_NICKNAME, DEFAULT_KAKAO_ID);
      LocalDateTime latestDate = FIXED_DATE.plusDays(1);
      LocalDateTime oldDate = FIXED_DATE.minusDays(1);
      persistKnowledge(user, SEARCH_TITLE, oldDate);
      persistKnowledge(user, SEARCH_TITLE, latestDate);

      // when
      List<Knowledge> knowledgeList = knowledgeSearchRepository.findByTitleContaining(user, SEARCH_TITLE);

      // then
      assertThat(knowledgeList).hasSize(2);
      assertThat(knowledgeList.get(0).getCreatedAt()).isAfter(knowledgeList.get(1).getCreatedAt());
    }
  }

  @Nested
  @DisplayName("지식 검색 실패 케이스")
  class SearchFailureTest {

    @DisplayName("존재하지 않는 유저로 검색 시, 빈 리스트를 반환한다.")
    @Test
    void returnEmptyList_WhenUserNotExists() {
      // given
      User unusedUser = persistUser("김원희", DEFAULT_KAKAO_ID);
      User realUser = persistUser(DEFAULT_NICKNAME, "999");
      persistKnowledge(realUser, SEARCH_TITLE, FIXED_DATE);

      // when
      List<Knowledge> result = knowledgeSearchRepository.findByTitleContaining(unusedUser, SEARCH_TITLE);

      // then
      assertThat(result).isEmpty();
    }

    @DisplayName("검색어에 해당하는 지식이 없을 경우, 빈 리스트를 반환한다.")
    @Test
    void returnEmptyList_WhenTitleDoesNotMatch() {
      // given
      User user = persistUser(DEFAULT_NICKNAME, DEFAULT_KAKAO_ID);
      persistKnowledge(user, UNMATCHED_TITLE_1, FIXED_DATE);
      persistKnowledge(user, UNMATCHED_TITLE_2, FIXED_DATE);

      // when
      List<Knowledge> result = knowledgeSearchRepository.findByTitleContaining(user, UNMATCHED_SEARCH_KEYWORD);

      // then
      assertThat(result).isEmpty();
    }
  }

  private void entityManagerClear() {
    em.flush();
    em.clear();
  }

  private Knowledge persistKnowledge(User user, String title, LocalDateTime fixedDate) {
    Knowledge knowledge = creatKnowledge(user, title, fixedDate);

    em.persist(knowledge);
    return knowledge;
  }

  private Knowledge creatKnowledge(User user, String title, LocalDateTime fixedDate) {
    return Knowledge.builder()
            .user(user)
            .title(title)
            .content(DEFAULT_CONTENT)
            .createdAt(fixedDate)
            .build();
  }

  private User persistUser(String nickname, String kakaoId) {
    User user = createUser(nickname, kakaoId);

    em.persist(user);
    return user;
  }

  private User createUser(String nickname, String kakaoId) {
    return User.builder()
            .nickname(nickname)
            .kakaoId(kakaoId)
            .build();
  }
}
