package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.entity.Knowledge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@Import(KnowledgeReadServiceImpl.class)
class KnowledgeServiceTest {

  private final static LocalDateTime FIXED_DATE = LocalDateTime.of(2025, 5, 9, 0, 0);
  private static final String TITLE = "제목";
  private static final String CONTENT = "내용";

  @InjectMocks
  KnowledgeReadService knowledgeReadService;

  @Mock
  KnowledgeReadRepository knowledgeReadRepository;

  @DisplayName("쿼리 스트링으로 전달받은 지식 id와 로그인된 유저의 id의 지식을 가지고 있는 지식 하나를 불러온다.")
  @Test
  void xx (){
    // given

    Knowledge mockKnowledge = Knowledge.builder()
            .title(TITLE)
            .content(CONTENT)
            .createdAt(FIXED_DATE)
            .build();
    given(knowledgeReadRepository.findKnowledge(user, id)).willReturn(
            mockKnowledge);

    // when
    KnowledgeDetailReseponseDto knowledgeDetailReseponseDto = knowledgeReadService.getKnowledge(id);

    //then
    assertThat(knowledgeDetailReseponseDto.getId().isEqualsTo(1);
    assertThat(knowledgeDetailReseponseDto.getTitle()).isEqualsTo(TITLE);
    assertThat(knowledgeDetailReseponseDto.getContent()).isEqualsTo(CONTENT);
    assertThat(knowledgeDetailReseponseDto.getCreateAt()).isEqaulsTo(FIXED_DATE);
  }
}
