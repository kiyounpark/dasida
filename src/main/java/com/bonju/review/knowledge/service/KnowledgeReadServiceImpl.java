package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.dto.KnowledgeDetailResponseDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.knowledge.repository.KnowledgeReadRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import com.bonju.review.util.enums.error_code.KnowledgeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KnowledgeReadServiceImpl implements KnowledgeReadService{

  private final KnowledgeReadRepository knowledgeReadRepository;
  private final UserService userService;

  @Override
  @Transactional(readOnly = true)
  public KnowledgeDetailResponseDto getKnowledgeById(Long id) {
    User user = userService.findUser();

    try {
      Knowledge knowledge = knowledgeReadRepository.findKnowledge(user, id)
              .orElseThrow(() -> new KnowledgeException(KnowledgeErrorCode.NOT_FOUND));

      return KnowledgeDetailResponseDto.builder()
              .id(knowledge.getId())
              .title(knowledge.getTitle())
              .content(knowledge.getContent())
              .createdAt(knowledge.getCreatedAt())
              .build();

    } catch (DataAccessException e) {
      throw new KnowledgeException(KnowledgeErrorCode.RETRIEVE_FAILED, e);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasRegisteredKnowledge() {
    User user = userService.findUser();

    try {
      return knowledgeReadRepository.hasRegisteredKnowledge(user);
    } catch (DataAccessException e) {
      throw new KnowledgeException(KnowledgeErrorCode.RETRIEVE_FAILED, e);
    }
  }
}
