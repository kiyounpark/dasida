package com.bonju.review.knowledge_quiz.workflow;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.service.KnowledgeRegistrationService;
import com.bonju.review.quiz.service.register.QuizAutoGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class KnowledgeQuizCreationWorkflow {

  private final KnowledgeRegistrationService knowledgeRegistrationService;
  private final QuizAutoGeneratorService quizAutoGeneratorService;

  @Transactional
  public Long registerKnowledgeAndGenerateQuizList(String title, String content) {
    Knowledge knowledge = knowledgeRegistrationService.registerKnowledge(title, content);
    quizAutoGeneratorService.generateQuiz(knowledge, content);
    return knowledge.getId();
  }
}
