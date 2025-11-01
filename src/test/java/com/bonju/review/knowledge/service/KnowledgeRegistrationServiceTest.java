package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.dto.KnowledgeRegisterRequestDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.knowledge.repository.KnowledgeRegistrationRepository;
import com.bonju.review.knowledge.mapper.KnowledgeMapper;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("지식 등록 서비스 테스트")
class KnowledgeRegistrationServiceTest {

  @Mock
  private KnowledgeRegistrationRepository repository;

  @Mock
  private UserService userService;

  @Mock
  private KnowledgeMapper knowledgeMapper;

  @InjectMocks
  private KnowledgeRegistrationServiceImpl knowledgeRegistrationService;

  @Test
  @DisplayName("지식을 등록하면 저장된 ID를 반환한다")
  void registerKnowledge_returnsSavedId() {
    // given
    String title = "테스트 제목";
    String content = "테스트 내용";
    KnowledgeRegisterRequestDto requestDto = KnowledgeRegisterRequestDto.builder()
            .title(title)
            .text(content)
            .images(List.of("https://cdn.test/knowledge-image.png"))
            .build();

    Knowledge savedKnowledge = Knowledge.builder()
            .title(title)
            .text(content)
            .build();

    User user = User.builder().build();
    given(userService.findUser()).willReturn(user);
    given(knowledgeMapper.toEntity(user, requestDto)).willReturn(savedKnowledge);
    given(repository.save(any(Knowledge.class))).willReturn(savedKnowledge);

    // when
    Knowledge result = knowledgeRegistrationService.registerKnowledge(requestDto);

    // then
    assertThat(result.getTitle()).isEqualTo(title);
    assertThat(result.getText()).isEqualTo(content);
  }

  @Test
  @DisplayName("저장 중 DataAccessException 발생 시 KnowledgeException으로 래핑한다")
  void registerKnowledge_throwsKnowledgeException_onDataAccess() {
    // given
    User user = User.builder().build();
    KnowledgeRegisterRequestDto requestDto = KnowledgeRegisterRequestDto.builder()
            .title("타이틀")
            .text("내용")
            .images(List.of("https://cdn.test/knowledge-image.png"))
            .build();

    given(userService.findUser()).willReturn(user);
    given(knowledgeMapper.toEntity(user, requestDto)).willReturn(Knowledge.builder().build());
    given(repository.save(any(Knowledge.class)))
            .willThrow(new DataAccessException("DB 오류") {});

    // when & then
    assertThrows(KnowledgeException.class, () ->
            knowledgeRegistrationService.registerKnowledge(requestDto)
    );
  }
}
