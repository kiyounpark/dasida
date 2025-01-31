package com.bonju.review.service;


import com.bonju.review.dto.KnowledgeImageRequestDto;
import com.bonju.review.dto.KnowledgeTextRequestDto;

public interface KnowledgeService {

    Long registerKnowledge(KnowledgeTextRequestDto knowledgeRequestDto);
    Long registerKnowledge(KnowledgeImageRequestDto knowledgeRequestDto);
}
