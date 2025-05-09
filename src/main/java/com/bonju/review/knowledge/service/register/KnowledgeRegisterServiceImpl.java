package com.bonju.review.knowledge.service.register;

import com.bonju.review.knowledge.dto.KnowledgeRegisterRequestDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.mapper.KnowledgeMapper;
import com.bonju.review.user.entity.User;
import com.bonju.review.knowledge.repository.register.KnowledgeRegisterRepository;
import com.bonju.review.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KnowledgeRegisterServiceImpl implements KnowledgeRegisterService {

    private final KnowledgeRegisterRepository knowledgeRegisterRepository;
    private final UserService userService;
    private final KnowledgeMapper knowledgeMapper;
    @Override
    @Transactional
    public Knowledge registerKnowledge(KnowledgeRegisterRequestDto knowledgeRegisterRequestDto) {
        User user = userService.findUser();
        Knowledge knowledge = knowledgeMapper.toEntity(user, knowledgeRegisterRequestDto);
        return knowledgeRegisterRepository.registerKnowledge(knowledge);
    }
}
