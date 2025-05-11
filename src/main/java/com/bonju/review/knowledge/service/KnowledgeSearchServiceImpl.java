package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.dto.KnowledgeItemResponseDto;
import com.bonju.review.knowledge.dto.KnowledgeSearchResponseDto;
import com.bonju.review.knowledge.repository.KnowledgeSearchRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KnowledgeSearchServiceImpl implements KnowledgeSearchService{

  private final KnowledgeSearchRepository knowledgeSearchRepository;
  private final UserService userService;

  @Override
  @Transactional
  public KnowledgeSearchResponseDto searchKnowledgeByTitle(String title) {
    User user = userService.findUser();

    List<KnowledgeItemResponseDto> knowledgeItemList = knowledgeSearchRepository.findByTitleContaining(user, title).stream()
            .map(knowledge -> KnowledgeItemResponseDto.builder()
                    .id(knowledge.getId())
                    .title(knowledge.getTitle())
                    .createAt(knowledge.getCreatedAt())
                    .build())
            .toList();

    return KnowledgeSearchResponseDto.builder()
            .list(knowledgeItemList)
            .build();
  }
}
