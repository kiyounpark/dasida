package com.bonju.review.knowledge_quiz.service;

import com.bonju.review.knowledge.dto.KnowledgeRegisterRequestDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.service.register.KnowledgeRegisterService;
import com.bonju.review.quiz.service.register.QuizRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KnowledgeQuizServiceImpl implements KnowledgeQuizService {

    private final KnowledgeRegisterService knowledgeRegisterService;
    private final QuizRegisterService quizService;


    @Override
    @Transactional
    public void registerKnowledgeAndQuiz(KnowledgeRegisterRequestDto knowledgeRegisterRequestDto) {
        Knowledge knowledge = knowledgeRegisterService.registerKnowledge(knowledgeRegisterRequestDto);
        quizService.createQuiz(knowledge);
    }
}
