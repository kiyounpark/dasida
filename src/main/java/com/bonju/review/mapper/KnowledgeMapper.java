package com.bonju.review.mapper;

import com.bonju.review.dto.KnowledgeDto;
import com.bonju.review.entity.Knowledge;
import com.bonju.review.entity.User;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeMapper {

    public Knowledge toEntity(User user, KnowledgeDto knowledgeDto) {
        return new Knowledge(
                user,
                knowledgeDto.getTitle(),
                knowledgeDto.getDescription()
        );
    }
}
