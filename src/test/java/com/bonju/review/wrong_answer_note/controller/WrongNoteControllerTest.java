package com.bonju.review.wrong_answer_note.controller;

import com.bonju.review.slack.SlackErrorMessageFactory;
import com.bonju.review.wrong_answer_note.WrongNoteService;
import com.bonju.review.wrong_answer_note.dto.WrongAnswerGroupResponseDto;
import com.bonju.review.wrong_answer_note.dto.WrongAnswerResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WrongNoteController.class)
class WrongNoteControllerTest {

  public static final String END_POINT = "/wrong-notes";
  @Autowired  MockMvc mockMvc;
  @Autowired  ObjectMapper objectMapper;

  @MockitoBean
  WrongNoteService wrongNoteService;

  @MockitoBean
  SlackErrorMessageFactory slackErrorMessageFactory;

  @Test
  @DisplayName("loadWrongNote() → 200 OK & 기대 DTO JSON 반환")
  @WithMockUser
  void loadWrongNote_returns200AndDto() throws Exception {
    // given: 서비스가 정상 DTO를 반환하도록 스텁
    List<WrongAnswerGroupResponseDto> expected = List.of(
            new WrongAnswerGroupResponseDto(
                    101L,                      // quizId
                    "2 + 2 = ?",               // question
                    "4",                       // answer
                    List.of(
                            new WrongAnswerResponseDto(1L, false, "3", 1)
                    )
            )
    );
    given(wrongNoteService.loadWrongNote()).willReturn(expected);

    // when & then
    String expectedJson = objectMapper.writeValueAsString(expected);
    mockMvc.perform(get(END_POINT)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(
                    expectedJson));
  }

  @Test
  @DisplayName("loadWrongNote() - 오답이 없으면 200 OK 와 빈 배열을 반환한다")
  @WithMockUser
  void loadWrongNote_returnsEmptyList() throws Exception {
    // given
    given(wrongNoteService.loadWrongNote()).willReturn(List.of());

    // when & then
    mockMvc.perform(get(END_POINT)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));   // 빈 JSON 배열 검증
  }
}