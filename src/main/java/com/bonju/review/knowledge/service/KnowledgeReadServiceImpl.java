package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.dto.KnowledgeDetailResponseDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.knowledge.repository.KnowledgeReadRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KnowledgeReadServiceImpl implements KnowledgeReadService{

  public static final String ERROR_MASSAGE = "지식을 찾을 수 없습니다";
  private final KnowledgeReadRepository knowledgeReadRepository;
  private final UserService userService;

  @Override
  public KnowledgeDetailResponseDto getKnowledgeById(Long id) {
    User user = userService.findUser();

    Knowledge knowledge = knowledgeReadRepository.findKnowledge(user, id)
            .orElseThrow(() -> new KnowledgeException(ERROR_MASSAGE));


    return KnowledgeDetailResponseDto.builder()
            .id(knowledge.getId())
            .title(knowledge.getTitle())
            .content(knowledge.getContent())
            .createdAt(knowledge.getCreatedAt())
            .build();
  }
}
