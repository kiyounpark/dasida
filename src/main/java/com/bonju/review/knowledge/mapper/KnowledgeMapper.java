package com.bonju.review.knowledge.mapper;

import com.bonju.review.knowledge.dto.KnowledgeRegisterRequestDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.entity.KnowledgeImage;
import com.bonju.review.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class KnowledgeMapper {

    public Knowledge toEntity(User user, KnowledgeRegisterRequestDto knowledgeRegisterRequestDto) {
        List<KnowledgeImage> knowledgeImages = Optional.ofNullable(knowledgeRegisterRequestDto.getImages())
                .orElseGet(List::of)
                .stream()
                .map(imageUrl -> KnowledgeImage.builder().imageUrl(imageUrl).build())
                .toList();

        return Knowledge.builder()
                .user(user)
                .title(knowledgeRegisterRequestDto.getTitle())
                .text(knowledgeRegisterRequestDto.getText())
                .createdAt(LocalDateTime.now())
                .knowledgeImages(knowledgeImages)
                .build();
    }
}
