package com.bonju.review.wrongnote.integration;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.testsupport.security.WithMockKakaoUser;
import com.bonju.review.user.entity.User;
import com.bonju.review.useranswer.entity.UserAnswer;
import com.bonju.review.wronganswernote.dto.WrongAnswerGroupResponseDto;
import com.bonju.review.wronganswernote.dto.WrongAnswerResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class WrongNoteIntegrationTest {

  private static final String END_POINT = "/wrong-notes";

  @Autowired MockMvc mockMvc;
  @Autowired EntityManager em;
  @Autowired ObjectMapper objectMapper;

  @Test
  @DisplayName("통합: 오답 1건이 있으면 200 OK & DTO 리스트(JSON) 반환")
  @WithMockKakaoUser(kakaoId = "123")
  void loadWrongNote_returnsExpectedJson() throws Exception {

    /* ──────────────── given ──────────────── */

    // 1) 사용자
    User user = User.builder()
            .kakaoId("123")
            .build();
    em.persist(user);

    // 2) 지식
    Knowledge knowledge = Knowledge.builder()
            .user(user)
            .title("산수")
            .text("2+2 단원")
            .createdAt(LocalDateTime.now())
            .build();
    em.persist(knowledge);

    // 3) 퀴즈
    Quiz quiz = Quiz.builder()
            .user(user)
            .knowledge(knowledge)
            .question("2 + 2 = ?")
            .answer("4")
            .hint("사칙연산")
            .build();
    em.persist(quiz);

    // 4) 오답 1건
    UserAnswer wrong = UserAnswer.builder()
            .user(user)
            .quiz(quiz)
            .answer("3")        // 틀린 답
            .dayType(1)
            .isCorrect(false)
            .build();
    em.persist(wrong);

    em.flush();
    em.clear();

    /* ───────── expected DTO 구성 ───────── */

    List<WrongAnswerGroupResponseDto> expected = List.of(
            new WrongAnswerGroupResponseDto(
                    quiz.getId(),
                    "2 + 2 = ?",
                    "4",
                    List.of(
                            new WrongAnswerResponseDto(
                                    wrong.getId(),
                                    false,
                                    "3",
                                    1
                            )
                    )
            )
    );
    String expectedJson = objectMapper.writeValueAsString(expected);

    /* ───────────── when - then ───────────── */

    mockMvc.perform(get(END_POINT).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));   // 구조적 JSON 비교
  }

  @Test
  @DisplayName("통합: 오답이 없으면 200 OK & 빈 배열 반환")
  @WithMockKakaoUser(kakaoId = "321")
  void loadWrongNote_returnsEmptyJsonArray() throws Exception {

    /* ──────────────── given ──────────────── */

    // 1) 사용자
    User user = User.builder()
            .kakaoId("321")
            .build();
    em.persist(user);

    // 2) 지식
    Knowledge knowledge = Knowledge.builder()
            .user(user)
            .title("산수")
            .text("2+2 단원")
            .createdAt(LocalDateTime.now())
            .build();
    em.persist(knowledge);

    // 3) 퀴즈 (정답만 존재 → 오답 없음)
    Quiz quiz = Quiz.builder()
            .user(user)
            .knowledge(knowledge)
            .question("2 + 2 = ?")
            .answer("4")
            .hint("사칙연산")
            .build();
    em.persist(quiz);

    // 4) 정답 1건(→ 오답 필터에 걸리지 않음)
    UserAnswer correct = UserAnswer.builder()
            .user(user)
            .quiz(quiz)
            .answer("4")        // 정답
            .dayType(1)
            .isCorrect(true)
            .build();
    em.persist(correct);

    em.flush();
    em.clear();

    /* ───────────── when - then ───────────── */

    mockMvc.perform(get(END_POINT).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));   // 빈 JSON 배열
  }
}