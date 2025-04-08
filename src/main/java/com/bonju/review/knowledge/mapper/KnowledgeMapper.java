package com.bonju.review.knowledge.mapper;

import com.bonju.review.knowledge.dto.KnowledgeRequestDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeMapper {

    public Knowledge toEntity(User user, KnowledgeRequestDto knowledgeRequestDto) {
        return  Knowledge.builder()
                .user(user)
                .title(knowledgeRequestDto.getTitle())
                .content(knowledgeRequestDto.getContent())
                .build();


    }
}
