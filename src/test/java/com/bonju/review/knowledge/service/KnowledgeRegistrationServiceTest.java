package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.knowledge.repository.KnowledgeRegistrationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("지식 등록 서비스 테스트")
class KnowledgeRegistrationServiceTest {

  @Mock
  private KnowledgeRegistrationRepository repository;

  @InjectMocks
  private KnowledgeRegistrationServiceImpl knowledgeRegistrationService;

  @Test
  @DisplayName("지식을 등록하면 저장된 ID를 반환한다")
  void registerKnowledge_returnsSavedId() {
    // given
    String title = "테스트 제목";
    String content = "테스트 내용";

    given(repository.save(any(Knowledge.class))).willReturn(1L);

    // when
    Long result = knowledgeRegistrationService.registerKnowledge(title, content);

    // then
    assertThat(result).isEqualTo(1L);
    verify(repository).save(argThat(entity ->
            title.equals(entity.getTitle()) &&
                    content.equals(entity.getContent())
    ));
  }

  @Test
  @DisplayName("저장 중 DataAccessException 발생 시 KnowledgeException으로 래핑한다")
  void registerKnowledge_throwsKnowledgeException_onDataAccess() {
    // given
    given(repository.save(any(Knowledge.class)))
            .willThrow(new DataAccessException("DB 오류") {});

    // when & then
    assertThrows(KnowledgeException.class, () ->
            knowledgeRegistrationService.registerKnowledge("타이틀", "내용")
    );
  }
}
