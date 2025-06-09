package com.bonju.review.useranswer;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.useranswer.entity.UserAnswer;
import com.bonju.review.useranswer.dto.UserAnswerRequestDto;
import com.bonju.review.useranswer.dto.UserAnswerResponseDto;
import com.bonju.review.useranswer.repository.UserAnswerRepository;
import com.bonju.review.testsupport.security.WithMockKakaoUser;
import com.bonju.review.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;

/**
 *  ✅ 실제 흐름
 *      MockBean 은 Slack 만 대체, 나머지는 전부 실제 빈·DB 사용
 *  ✅ H2(in-mem) + @Transactional 로 매 테스트 후 롤백
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class UserAnswerIntegrationTest {

  private static final int  DAY_TYPE = 0;
  private static final String RIGHT   = "정답";
  private static final String WRONG   = "오답";

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @Autowired EntityManager em;
  @MockitoBean
  UserAnswerRepository userAnswerRepository;


  /** 테스트용 유저‧퀴즈 한 세트를 생성해서 ID 저장  */
  Long quizId;

  @BeforeEach
  void setUp() {
    User user = User.builder().kakaoId("123").build();
    em.persist(user);           // flush() 필요 X

    Knowledge knowledge = Knowledge.builder().user(user).build();
    em.persist(knowledge);

    Quiz quiz = Quiz.builder()
            .user(user)
            .knowledge(knowledge)
            .answer(RIGHT)
            .build();
    em.persist(quiz);

    quizId = quiz.getId();      // persist 시점에 PK 가 할당되므로 안전
  }

  /* ----------------------------------------------------------------
   *  [1]   정답 / 오답 → 200 OK & isCorrect 플래그 확인
   * ----------------------------------------------------------------*/
  @ParameterizedTest(name = "플래그 = {0}")
  @MethodSource("answerProvider")
  @WithMockKakaoUser(kakaoId = "123")
  void submit_answer_flow(boolean expectedCorrect, String givenAnswer) throws Exception {
    String body = objectMapper.writeValueAsString(
            new UserAnswerRequestDto(givenAnswer, DAY_TYPE));

    String rsp = mockMvc.perform(post("/quizzes/{id}/answers", quizId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    UserAnswerResponseDto dto = objectMapper.readValue(rsp, UserAnswerResponseDto.class);
    assertThat(dto.isCorrect()).isEqualTo(expectedCorrect);
  }

  private static Stream<Arguments> answerProvider() {
    return Stream.of(
            Arguments.of(true, RIGHT),
            Arguments.of(false, WRONG)
    );
  }

  /* ----------------------------------------------------------------
   *  [2]   입력 검증 실패 → 400 Bad Request  (실제 Validator 동작 확인)
   * ----------------------------------------------------------------*/
  @ParameterizedTest(name = "잘못된 JSON → 400 : {0}")
  @MethodSource("invalidBodies")
  @WithMockKakaoUser(kakaoId = "123")
  void submit_validation_fail(String invalidJson) throws Exception {

    mockMvc.perform(post("/quizzes/{id}/answers", quizId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
            .andExpect(status().isBadRequest());
  }

  private static Stream<String> invalidBodies() {
    return Stream.of(
            // ① answer가 빈 문자열
            """
            {"answer": "", "day_type": 0}
            """,

            // ② answer가 공백만 존재
            """
            {"answer": "   ", "day_type": 0}
            """,

            // ③ answer 필드 누락
            """
            {"day_type": 0}
            """,

            // ④ day_type 필드 누락  ➜  Integer 이므로 @NotNull 위반
            """
            {"answer": "정답"}
            """,

            // ⑤ day_type = null      ➜  @NotNull 위반
            """
            {"answer": "정답", "day_type": null}
            """
    );
  }

  /* ⚠️ 실패 플로우 전용 컨텍스트 */
  @Nested
  @Transactional
  class DbFailureFlow {

    @Test
    @DisplayName("DB 저장 실패 시 500 반환 - Slack 전송 (cause 포함)")
    @WithMockKakaoUser(kakaoId = "123")
    void db_failure_returns_500_and_sends_slack_with_cause() throws Exception {

      /* ⚠️ 1️⃣  실제 흐름과 유사하게:  SQLException → DataIntegrityViolationException  */
      SQLException sqlEx = new SQLException("duplicate key value violates constraint", "23505");
      DataIntegrityViolationException wrapped =
              new DataIntegrityViolationException("simulate failure", sqlEx);

      willThrow(wrapped)
              .given(userAnswerRepository)
              .save(any(UserAnswer.class));

      String body = objectMapper.writeValueAsString(
              new UserAnswerRequestDto(RIGHT, DAY_TYPE));

      /* 3️⃣ 요청 수행 */
      mockMvc.perform(post("/quizzes/{id}/answers", quizId)
                      .with(csrf())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(body))
              .andExpect(status().isInternalServerError());
    }
  }
}