package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.dto.KnowledgeDetailResponseDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.knowledge.repository.KnowledgeReadRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import com.bonju.review.util.enums.error_code.KnowledgeErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KnowledgeReadServiceImpl implements KnowledgeReadService{

  private final KnowledgeReadRepository knowledgeReadRepository;
  private final UserService userService;

  @Override
  @Transactional
  public KnowledgeDetailResponseDto getKnowledgeById(Long id) {
    User user = userService.findUser();

    Knowledge knowledge = knowledgeReadRepository.findKnowledge(user, id)
            .orElseThrow(() -> new KnowledgeException(KnowledgeErrorCode.NOT_FOUND));


    return KnowledgeDetailResponseDto.builder()
            .id(knowledge.getId())
            .title(knowledge.getTitle())
            .content(knowledge.getContent())
            .createdAt(knowledge.getCreatedAt())
            .build();
  }
}
