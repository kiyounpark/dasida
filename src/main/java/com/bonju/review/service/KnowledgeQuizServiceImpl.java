package com.bonju.review.service;

import com.bonju.review.dto.KnowledgeRequestDto;
import com.bonju.review.entity.Knowledge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KnowledgeQuizServiceImpl implements KnowledgeQuizService {

    private final KnowledgeService knowledgeService;
    private final QuizRegisterService quizService;


    @Override
    @Transactional
    public void registerKnowledgeAndQuiz(KnowledgeRequestDto knowledgeRequestDto) {
        Knowledge knowledge = knowledgeService.registerKnowledge(knowledgeRequestDto);
        quizService.createQuiz(knowledge);
    }
}
