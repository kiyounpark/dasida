package com.bonju.review.knowledge.service.knowledge_list;

import com.bonju.review.knowledge.converter.MarkdownConverter;
import com.bonju.review.knowledge.dto.DayKnowledgeResponseDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.knowledge.repository.knowledge_list.TodayKnowledgeListRepository;
import com.bonju.review.knowledge.vo.SingleDayRange;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import com.bonju.review.util.enums.DayType;
import com.bonju.review.util.enums.error_code.KnowledgeErrorCode;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodayKnowledgeListServiceImpl implements TodayKnowledgeListService {

    private final TodayKnowledgeListRepository todayKnowledgeListRepository;
    private final UserService userService;
    private final MarkdownConverter markdownConverter;

    @Override
    @Transactional(readOnly = true)
    public ImmutableList<DayKnowledgeResponseDto> getAllDayKnowledges() {
        User user = userService.findUser();
        ImmutableList.Builder<DayKnowledgeResponseDto> dtoListBuilder = ImmutableList.builder();

        for (DayType dayType : DayType.values()) {
            dtoListBuilder.addAll(fetchDtoListByDayType(user, dayType));
        }

        return dtoListBuilder.build();
    }

    private List<DayKnowledgeResponseDto> fetchDtoListByDayType(User user, DayType dayType) {
        try {
            int daysAgo = dayType.getDaysAgo();
            LocalDate start = LocalDate.now().minusDays(daysAgo);
            SingleDayRange dayRange = new SingleDayRange(start);

            List<Knowledge> knowledgeList = todayKnowledgeListRepository.findKnowledgeListByDateRange(user, dayRange);

            return knowledgeList.stream()
                    .map(knowledge -> DayKnowledgeResponseDto.builder()
                            .dayType(dayType.getDaysAgo())
                            .id(knowledge.getId())
                            .title(knowledge.getTitle())
                            .text(markdownConverter.convertTruncatedParagraph(knowledge.getText()))
                            .build())
                    .toList();
        } catch (DataAccessException e) {
            throw new KnowledgeException(KnowledgeErrorCode.NOT_FOUND, e);
        }
    }
}
