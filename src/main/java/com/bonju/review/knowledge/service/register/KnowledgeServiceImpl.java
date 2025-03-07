package com.bonju.review.knowledge.service.register;

import com.bonju.review.knowledge.dto.KnowledgeRequestDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.user.entity.User;
import com.bonju.review.knowledge.repository.register.KnowledgeRepository;
import com.bonju.review.user.service.UserService;
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
    public Knowledge registerKnowledge(KnowledgeRequestDto knowledgeRequestDto) {
        User user = userService.findUser();
        Knowledge knowledge = new Knowledge(user, knowledgeRequestDto.getTitle(), knowledgeRequestDto.getContentHtml());
        return knowledgeRepository.registerKnowledge(knowledge);
    }
}
