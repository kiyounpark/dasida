package com.bonju.review.knowledge.mapper;

import com.bonju.review.knowledge.dto.KnowledgeRegisterRequestDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class KnowledgeMapper {

    public Knowledge toEntity(User user, KnowledgeRegisterRequestDto knowledgeRegisterRequestDto) {
        return  Knowledge.builder()
                .user(user)
                .title(knowledgeRegisterRequestDto.getTitle())
                .text(knowledgeRegisterRequestDto.getText())
                .createdAt(LocalDateTime.now())
                .build();


    }
}
