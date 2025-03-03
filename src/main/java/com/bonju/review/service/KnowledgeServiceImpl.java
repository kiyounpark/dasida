package com.bonju.review.service;

import com.bonju.review.dto.KnowledgeRequestDto;
import com.bonju.review.entity.Knowledge;
import com.bonju.review.entity.User;
import com.bonju.review.repository.KnowledgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService{

    private final KnowledgeRepository knowledgeRepository;
    private final UserService userService;
    @Override
    @Transactional
    public Long registerKnowledge(KnowledgeRequestDto knowledgeRequestDto) {
        User user = userService.findUser();
        Knowledge knowledge = new Knowledge(user, knowledgeRequestDto.getTitle(), knowledgeRequestDto.getContentHtml());
        return knowledgeRepository.registerKnowledge(knowledge);
    }
}
