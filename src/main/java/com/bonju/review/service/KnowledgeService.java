package com.bonju.review.service;


import com.bonju.review.dto.KnowledgeImageRequestDto;
import com.bonju.review.dto.KnowledgeRequestDto;

public interface KnowledgeService {

    Long registerKnowledge(KnowledgeRequestDto knowledgeRequestDto);
    Long registerKnowledge(KnowledgeImageRequestDto knowledgeRequestDto);
}
