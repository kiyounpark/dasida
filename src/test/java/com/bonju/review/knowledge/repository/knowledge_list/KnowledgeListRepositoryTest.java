package com.bonju.review.knowledge.repository.knowledge_list;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.repository.KnowledgeListRepository;
import com.bonju.review.knowledge.repository.KnowledgeListRepositoryJpa;
import com.bonju.review.user.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(KnowledgeListRepositoryJpa.class)
class KnowledgeListRepositoryTest {
  private static final int DEFAULT_OFFSET = 0;
  private static final int LIMIT = 10; // LIMIT + 1
  private static final int PAGING_LIMIT_PLUS_ONE = LIMIT + 1; // LIMIT + 1


  private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 5, 1, 0, 0);

  @Autowired
  KnowledgeListRepository knowledgeListRepository;

  @Autowired
  EntityManager em;

  @ParameterizedTest
  @ValueSource(ints = {1, 3, 7, 10, 11})
  @DisplayName("페이징된 갯수가 명확한지 확인한다")
  void findKnowledgeList_ReturnListLength_WhenFetchKnowledgeList(int number) {
    LocalDateTime now = LocalDateTime.now();
    User user = User.builder()
            .kakaoId("1")
            .nickname("박기윤")
            .build();

    //given
    List<Knowledge> mockKnowledgeList = IntStream.range(0, number)
            .mapToObj(i -> Knowledge.builder()
                    .user(user)
                    .title("지식 " + i)
                    .content("지식 " + i)
                    .createdAt(now.plusDays(i))
                    .build())
            .toList();

    em.persist(user);

    for (Knowledge knowledge : mockKnowledgeList) {
      em.persist(knowledge);
    }
    em.flush();
    em.clear();

    //when
    List<Knowledge> knowledgeList = knowledgeListRepository.findKnowledgeList(
            user,
            DEFAULT_OFFSET,
            PAGING_LIMIT_PLUS_ONE
    );

    //then
    assertThat(knowledgeList).hasSizeLessThanOrEqualTo(PAGING_LIMIT_PLUS_ONE);
  }

  @DisplayName("불러온 지식 리스트가 특정 유저의 지식리스트 만 잘 가져오는지 확인한다.")
  @Test
  void findKnowledgeList_Return_WhenFetchKnowledgeList(){
    //given
    User kiyoon = User.builder()
            .kakaoId("1")
            .nickname("kiyoon")
            .build();

    User wonhee = User.builder()
            .kakaoId("2")
            .nickname("wonhee")
            .build();

    em.persist(kiyoon);
    em.persist(wonhee);


    List<Knowledge> kiyoonKnowledgeList = IntStream.rangeClosed(1, 3)
            .mapToObj(i -> Knowledge.builder()
                    .user(kiyoon)
                    .createdAt(FIXED_TIME)
                    .content("지식 " + i)
                    .title("제목 " + i)
                    .build()
            ).toList();


    List<Knowledge> wonheeKnowledgeList = IntStream.rangeClosed(1, 3)
            .mapToObj(i -> Knowledge.builder()
                    .user(wonhee)
                    .createdAt(FIXED_TIME)
                    .content("지식 " + i)
                    .title("제목 " + i)
                    .build()
            ).toList();

    for (Knowledge kiyoonKnowledge : kiyoonKnowledgeList) {
      em.persist(kiyoonKnowledge);
    }

    for (Knowledge wonheeKnowledge : wonheeKnowledgeList) {
      em.persist(wonheeKnowledge);
    }
    em.flush();
    em.clear();

    //when
    List<Knowledge> knowledgeList = knowledgeListRepository.findKnowledgeList(kiyoon, DEFAULT_OFFSET, PAGING_LIMIT_PLUS_ONE);

    //then
    assertThat(knowledgeList)
            .hasSize(kiyoonKnowledgeList.size())
            .allSatisfy(k -> assertThat(k.getUser().getId()).isEqualTo(kiyoon.getId()));
  }

    //when
    @DisplayName("지식을 보유하지 않은 유저가 지식 리스트를 요청하면 빈 리스트를 반환한다")
    @Test
    void findKnowledgeList_ReturnEmptyList_WhenUserHasNoKnowledge(){
      //given
      User wonhee = User.builder()
              .kakaoId("1")
              .nickname("wonhee")
              .build();

      User kiyoon = User.builder()
              .kakaoId("2")
              .nickname("kiyoon")
              .build();

      em.persist(wonhee);
      em.persist(kiyoon);

      Knowledge wonheeKnowledge = Knowledge.builder()
              .user(wonhee)
              .createdAt(FIXED_TIME)
              .title("제목")
              .content("내용")
              .build();

      em.persist(wonheeKnowledge);
      em.flush();
      em.clear();

      //when
      List<Knowledge> knowledgeList = knowledgeListRepository.findKnowledgeList(kiyoon, DEFAULT_OFFSET, PAGING_LIMIT_PLUS_ONE);

      //then
      assertThat(knowledgeList).isEmpty();
    }

  @DisplayName("만약 offset db에서 반환하는 list.length 수보다 많으면, 빈 리스트를 반환한다.")
  @Test
  void findKnowledgeList_ReturnEmptyList_WhenBigOffset(){
    // given
    User user = User.builder()
            .nickname("null")
            .kakaoId("12")
            .build();

    Knowledge knowledge = Knowledge.builder()
            .user(user)
            .createdAt(FIXED_TIME)
            .title("제목")
            .content("내용")
            .build();

    em.persist(user);
    em.persist(knowledge);
    em.flush();
    em.clear();

    // when
    List<Knowledge> knowledgeList = knowledgeListRepository.findKnowledgeList(null, 10, 11);

    // then
    assertThat(knowledgeList).isEmpty();
  }
}




