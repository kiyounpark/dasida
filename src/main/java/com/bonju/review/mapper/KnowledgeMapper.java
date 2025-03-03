package com.bonju.review.mapper;

import com.bonju.review.dto.KnowledgeRequestDto;
import com.bonju.review.entity.Knowledge;
import com.bonju.review.entity.User;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeMapper {

    public Knowledge toEntity(User user, KnowledgeRequestDto knowledgeRequestDto) {
        return new Knowledge(
                user,
                knowledgeRequestDto.getTitle(),
                knowledgeRequestDto.getContentHtml()
        );
    }
}
