package com.bonju.review.knowledge.service.knowledges;

import com.bonju.review.knowledge.converter.MarkdownConverter;
import com.bonju.review.knowledge.dto.DayKnowledgeResponseDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.knowledge.repository.knowledges.KnowledgesRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import com.bonju.review.util.enums.DayType;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgesService {

    private final KnowledgesRepository knowledgesRepository;
    private final UserService userService;
    private final MarkdownConverter markdownConverter;

    @Override
    @Transactional(readOnly = true)
    public ImmutableList<DayKnowledgeResponseDto> getAllDayKnowledges() {
        User user = userService.findUser();
        ImmutableList.Builder<DayKnowledgeResponseDto> dtoListBuilder = ImmutableList.builder();

        for (DayType dayType : DayType.values()) {
            dtoListBuilder.addAll(fetchDtosByDayType(user, dayType));
        }

        return dtoListBuilder.build();
    }

    private List<DayKnowledgeResponseDto> fetchDtosByDayType(User user, DayType dayType) {
        try {
            List<Knowledge> knowledgeList = knowledgesRepository.findKnowledgesByDaysAgo(user, dayType.getDaysAgo());

            return knowledgeList.stream()
                    .map(knowledge -> DayKnowledgeResponseDto.builder()
                            .dayType(dayType.getDaysAgo())
                            .id(knowledge.getId())
                            .title(knowledge.getTitle())
                            .content(markdownConverter.convertTruncatedParagraph(knowledge.getContent()))
                            .build())
                    .toList();
        } catch (DataAccessException e) {
            throw new KnowledgeException("지식 조회 중 오류가 발생했습니다: " + dayType.name(), e);
        }
    }
}
