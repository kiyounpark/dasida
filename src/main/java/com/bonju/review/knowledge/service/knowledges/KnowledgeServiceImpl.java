// KnowledgeServiceImpl.java
package com.bonju.review.knowledge.service.knowledges;

import com.bonju.review.knowledge.converter.MarkdownConverter;
import com.bonju.review.knowledge.dto.DayKnowledgeResponseDto;
import com.bonju.review.knowledge.repository.knowledges.KnowledgesRepository;
import com.bonju.review.knowledge.vo.DayKnowledgeResponses;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
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
        DayKnowledgeResponses responses = DayKnowledgeResponses.from(user, knowledgesRepository, markdownConverter);
        return responses.asList();
    }
}
