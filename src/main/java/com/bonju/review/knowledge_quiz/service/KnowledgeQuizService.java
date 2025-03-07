package com.bonju.review.knowledge_quiz.service;

import com.bonju.review.knowledge.dto.KnowledgeRequestDto;

public interface KnowledgeQuizService {
    void registerKnowledgeAndQuiz(KnowledgeRequestDto knowledgeRequestDto);
}
