package com.bonju.review.wronganswernote.service;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.useranswer.entity.UserAnswer;
import com.bonju.review.useranswer.service.UserAnswerService;
import com.bonju.review.wronganswernote.dto.WrongAnswerGroupResponseDto;
import com.bonju.review.wronganswernote.dto.WrongAnswerResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WrongNoteService {

  private final UserAnswerService userAnswerService;
  public List<WrongAnswerGroupResponseDto> loadWrongNote() {

    /* 1) 사용자 답안 전부 조회 (정답·오답 포함) */
    List<UserAnswer> allAnswers = userAnswerService.findAll();

    /* 2) 만약 조회시 비어있다면, 빈 리스트 반환 */
    if(allAnswers.isEmpty()) {
      return List.of();
    }

    /* 3) quizId 기준 그룹핑 — LinkedHashMap 으로 순서(내림차순) 유지 */
    Map<Long, List<UserAnswer>> byQuiz = allAnswers.stream()
            .collect(Collectors.groupingBy(
                    userAnswer -> userAnswer.getQuiz().getId(),
                    LinkedHashMap::new,
                    Collectors.toList()));

    /* 4) 오답이 하나라도 있는 퀴즈만 필터링 → DTO 변환 */
    return byQuiz.values().stream()
            .filter(list -> list.stream().anyMatch(UserAnswer::isWrong))
            .map(this::toGroupDto)
            .toList();
  }

  /* ───────────────── private helpers ───────────────── */

  /** 하나의 퀴즈(answers 리스트) → 그룹 DTO 변환 */
  private WrongAnswerGroupResponseDto toGroupDto(List<UserAnswer> answers) {
    Quiz quiz = answers.getFirst().getQuiz();   // 동일 quiz 객체

    List<WrongAnswerResponseDto> answerDtoList = answers.stream()
            .map(this::toAnswerDto)
            .toList();

    return new WrongAnswerGroupResponseDto(
            quiz.getId(),
            quiz.getQuestion(),
            quiz.getAnswer(),
            answerDtoList
    );
  }

  /** UserAnswer → WrongAnswerResponseDto 변환 */
  private WrongAnswerResponseDto toAnswerDto(UserAnswer userAnswer) {
    return new WrongAnswerResponseDto(
            userAnswer.getId(),         // userAnswerId
            userAnswer.isCorrect(),     // correct (정답 여부)
            userAnswer.getAnswer(),     // 사용자가 입력한 답
            userAnswer.getDayType()     // 복습 주기
    );
  }
}