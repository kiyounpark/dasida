package com.bonju.review.service;

import com.bonju.review.dto.KnowledgeRequestDto;

public interface KnowledgeQuizService {
    void registerKnowledgeAndQuiz(KnowledgeRequestDto knowledgeRequestDto);
}
