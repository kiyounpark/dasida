package com.bonju.review.home.workflow;


import com.bonju.review.home.dto.HomeResponseDto;
import com.bonju.review.knowledge.dto.DayKnowledgeResponseDto;
import com.bonju.review.knowledge.service.KnowledgeReadService;
import com.bonju.review.knowledge.service.knowledge_list.TodayKnowledgeListService;
import com.bonju.review.quiz.dto.DayQuizResponseDto;
import com.bonju.review.quiz.service.quizzes.QuizzesService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
public class HomeWorkflow {

  private final QuizzesService quizzesService;
  private final TodayKnowledgeListService todayKnowledgeListService;
  private final KnowledgeReadService knowledgeReadService;

  /** 홈 화면에 필요한 모든 데이터를 조합해 반환한다 */
  public HomeResponseDto buildHomeResponse() {
    boolean hasRegisteredKnowledge = knowledgeReadService.hasRegisteredKnowledge();
    List<DayQuizResponseDto> dayQuizzes = quizzesService.getAllDayQuizzes();
    List<DayKnowledgeResponseDto> dayKnowledges = todayKnowledgeListService.getAllDayKnowledges();
    return new HomeResponseDto(hasRegisteredKnowledge, dayQuizzes, dayKnowledges);
  }
}
