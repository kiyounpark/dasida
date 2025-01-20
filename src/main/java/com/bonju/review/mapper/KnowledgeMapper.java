package com.bonju.review.mapper;

import com.bonju.review.dto.KnowledgeDto;
import com.bonju.review.entity.Knowledge;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeMapper {

    public Knowledge toEntity(KnowledgeDto knowledgeDto) {
        return new Knowledge(
                knowledgeDto.getTitle(),
                knowledgeDto.getDescription()
        );
    }
}
