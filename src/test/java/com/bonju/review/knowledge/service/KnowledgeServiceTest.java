package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.dto.KnowledgeDetailResponseDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.repository.KnowledgeReadRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class KnowledgeServiceTest {

  private static final LocalDateTime FIXED_DATE = LocalDateTime.of(2025, 5, 9, 0, 0);
  private static final String TITLE = "제목";
  private static final String CONTENT = "내용";

  @InjectMocks
  KnowledgeReadServiceImpl knowledgeReadService;

  @Mock
  KnowledgeReadRepository knowledgeReadRepository;

  @Mock
  UserService userService;

  @DisplayName("쿼리 스트링으로 전달받은 지식 id와 로그인된 유저의 id의 지식을 가지고 있는 지식 하나를 불러온다.")
  @Test
  void should_return_knowledge_detail_dto_when_valid_user_and_id_given(){
    // given
    Long id = 1L;

    User user = User.builder()
            .kakaoId("1")
            .nickname("nickname")
            .build();

    Knowledge mockKnowledge = Knowledge.builder()
            .title(TITLE)
            .content(CONTENT)
            .createdAt(FIXED_DATE)
            .build();

    ReflectionTestUtils.setField(mockKnowledge, "id", id);

    given(userService.findUser()).willReturn(user);
    given(knowledgeReadRepository.findKnowledge(user, id)).willReturn(
            mockKnowledge);

    // when
    KnowledgeDetailResponseDto knowledgeDetailResponseDto = knowledgeReadService.getKnowledgeById(id);

    //then
    assertThat(knowledgeDetailResponseDto.getId()).isEqualTo(1L);
    assertThat(knowledgeDetailResponseDto.getTitle()).isEqualTo(TITLE);
    assertThat(knowledgeDetailResponseDto.getContent()).isEqualTo(CONTENT);
    assertThat(knowledgeDetailResponseDto.getCreatedAt()).isEqualTo(FIXED_DATE);
  }
}
