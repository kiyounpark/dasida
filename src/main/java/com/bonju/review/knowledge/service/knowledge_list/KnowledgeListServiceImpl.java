package com.bonju.review.knowledge.service.knowledge_list;

import com.bonju.review.knowledge.dto.KnowledgeListResponseDto;
import com.bonju.review.knowledge.dto.KnowledgeResponseDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.repository.KnowledgeListRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import com.bonju.review.util.dto.PagingResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KnowledgeListServiceImpl implements KnowledgeListService {

  private static final int LIMIT = 10;

  private final KnowledgeListRepository knowledgeListRepository;
  private final UserService userService;

  @Override
  public KnowledgeListResponseDto getKnowledgeList(int offset) {
    User user = userService.findUser();
    List<Knowledge> entities =
            knowledgeListRepository.findKnowledgeList(user ,offset, LIMIT + 1);

    boolean hasNext = entities.size() > LIMIT;
    if (hasNext) {
      entities = entities.subList(0, LIMIT);
    }

    List<KnowledgeResponseDto> dtoList = entities.stream()
            .map(knowledge -> KnowledgeResponseDto.builder()
                    .id(knowledge.getId())
                    .title(knowledge.getTitle())
                    .build())
            .toList();

    PagingResponseDto paging = PagingResponseDto.builder()
            .nextOffset(hasNext ? offset + LIMIT : null)
            .build();

    return KnowledgeListResponseDto.builder()
            .knowledgeList(dtoList)
            .page(paging)
            .build();
  }
}
