package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.dto.KnowledgeDetailResponseDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.knowledge.repository.KnowledgeReadRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import com.bonju.review.util.enums.error_code.KnowledgeErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
  void should_return_knowledge_detail_dto_when_valid_user_and_id_given() {
    // given
    Long id = 1L;
    User user = createUser();
    Knowledge entity = createKnowledge(id, user);

    given(userService.findUser()).willReturn(user);
    given(knowledgeReadRepository.findKnowledge(user, id))
            .willReturn(Optional.of(entity)); // ✅ 수정됨

    // when
    KnowledgeDetailResponseDto response = knowledgeReadService.getKnowledgeById(id);

    // then
    verify(knowledgeReadRepository).findKnowledge(user, id);
    assertThat(response.getId()).isEqualTo(id);
    assertThat(response.getTitle()).isEqualTo(TITLE);
    assertThat(response.getContent()).isEqualTo(CONTENT);
    assertThat(response.getCreatedAt()).isEqualTo(FIXED_DATE);
  }

  @DisplayName("지식이 존재하지 않으면 KnowledgeException 을 던진다")
  @Test
  void should_throw_exception_when_knowledge_not_found() {
    // given
    Long id = 999L; // ❗ 실제 값
    User user = createUser();

    given(userService.findUser()).willReturn(user);
    given(knowledgeReadRepository.findKnowledge(user, id))
            .willReturn(Optional.empty()); // ✅ 빈 Optional 명확히

    // when & then
    assertThatThrownBy(() -> knowledgeReadService.getKnowledgeById(id)) // ❗ 실제 값
            .isInstanceOf(KnowledgeException.class)
            .hasMessageContaining(KnowledgeErrorCode.NOT_FOUND.getMessage());

    verify(knowledgeReadRepository).findKnowledge(user, id);
  }

  @DisplayName("KnowledgeReadRepository에서 DataAccessException이 발생하면 KnowledgeException(RETRIEVE_FAILED)를 던진다")
  @Test
  void should_throw_knowledge_exception_when_repository_throws_data_access_exception() {
    // given
    Long id = 1L;
    User user = createUser();

    given(userService.findUser()).willReturn(user);
    given(knowledgeReadRepository.findKnowledge(user, id))
            .willThrow(new DataAccessException("DB 예외 발생") {});

    // when & then
    assertThatThrownBy(() -> knowledgeReadService.getKnowledgeById(id))
            .isInstanceOf(KnowledgeException.class)
            .hasMessage(KnowledgeErrorCode.RETRIEVE_FAILED.getMessage())
            .hasCauseInstanceOf(DataAccessException.class);

    verify(knowledgeReadRepository).findKnowledge(user, id);
  }

  @DisplayName("유저가 지식을 하나라도 등록했다면 true를 반환한다")
  @Test
  void hasRegisteredKnowledge_returns_true_when_exists() {
    // given
    User user = createUser();
    given(userService.findUser()).willReturn(user);
    given(knowledgeReadRepository.hasRegisteredKnowledge(user)).willReturn(true);

    // when
    boolean actual = knowledgeReadService.hasRegisteredKnowledge();

    // then
    assertThat(actual).isTrue();
    verify(knowledgeReadRepository).hasRegisteredKnowledge(user);
  }

  @DisplayName("유저가 지식을 하나도 등록하지 않았다면 false를 반환한다")
  @Test
  void hasRegisteredKnowledge_returns_false_when_empty() {
    // given
    User user = createUser();
    given(userService.findUser()).willReturn(user);
    given(knowledgeReadRepository.hasRegisteredKnowledge(user)).willReturn(false);

    // when
    boolean actual = knowledgeReadService.hasRegisteredKnowledge();

    // then
    assertThat(actual).isFalse();
    verify(knowledgeReadRepository).hasRegisteredKnowledge(user);
  }

  @DisplayName("Repository에서 DataAccessException이 발생하면 KnowledgeException(RETRIEVE_FAILED)을 던진다")
  @Test
  void hasRegisteredKnowledge_throws_knowledge_exception_when_repository_fails() {
    // given
    User user = createUser();
    given(userService.findUser()).willReturn(user);
    given(knowledgeReadRepository.hasRegisteredKnowledge(user))
            .willThrow(new DataAccessException("DB 예외 발생") {});

    // when & then
    assertThatThrownBy(() -> knowledgeReadService.hasRegisteredKnowledge())
            .isInstanceOf(KnowledgeException.class)
            .hasMessage(KnowledgeErrorCode.RETRIEVE_FAILED.getMessage())
            .hasCauseInstanceOf(DataAccessException.class);

    verify(knowledgeReadRepository).hasRegisteredKnowledge(user);
  }

  private User createUser() {

    return User.builder()
            .kakaoId("1")
            .nickname("nickname")
            .build();
  }

  private Knowledge createKnowledge(Long id, User user) {
    Knowledge knowledge = Knowledge.builder()
            .user(user)
            .title(TITLE)
            .text(CONTENT)
            .createdAt(FIXED_DATE)
            .build();
    ReflectionTestUtils.setField(knowledge, "id", id);
    return knowledge;
  }
}
