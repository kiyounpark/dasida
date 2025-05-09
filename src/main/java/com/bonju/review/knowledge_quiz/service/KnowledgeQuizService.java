package com.bonju.review.knowledge_quiz.service;

import com.bonju.review.knowledge.dto.KnowledgeRegisterRequestDto;

public interface KnowledgeQuizService {
    void registerKnowledgeAndQuiz(KnowledgeRegisterRequestDto knowledgeRegisterRequestDto);
}
