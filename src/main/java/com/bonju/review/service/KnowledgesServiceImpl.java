package com.bonju.review.service;

import com.bonju.review.dto.DayKnowledgeResponseDto;
import com.bonju.review.entity.Knowledge;
import com.bonju.review.entity.User;
import com.bonju.review.enums.DayType;
import com.bonju.review.repository.KnowledgesRepository;
import com.bonju.review.repository.KnowledgesRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KnowledgesServiceImpl implements KnowledgesService {

    private final KnowledgesRepository knowledgesRepository;
    private final UserService userService;
    @Override
    public List<DayKnowledgeResponseDto> getAllDayKnowledges() {
        User user = userService.findUser();
        List<DayKnowledgeResponseDto> result = new ArrayList<>();

        for (DayType dayType : DayType.values()) {
            int daysAgo = dayType.getDaysAgo();
            List<Knowledge> knowledges = knowledgesRepository.findKnowledgesByDaysAgo(user, daysAgo);

            for (Knowledge knowledge : knowledges) {
                // 🎯 Jsoup을 사용해 p 태그 내용만 가져오기
                String contentFromPTag = extractPTagContent(knowledge.getContentHtml());
                String trimmedContent = trimContent(contentFromPTag, 100);

                result.add(new DayKnowledgeResponseDto(
                        dayType.getDaysAgo(),
                        knowledge.getId(),
                        knowledge.getTitle(),
                        trimmedContent
                ));
            }
        }
        return result;
    }

    // 🎯 Jsoup으로 p 태그의 텍스트만 추출하는 메서드
    private String extractPTagContent(String html) {
        Document document = Jsoup.parse(html);
        Elements paragraphs = document.select("p");
        return paragraphs.text();
    }

    // 🎯 길이 제한 메서드
    private String trimContent(String content, int maxLength) {
        if (content.length() > maxLength) {
            return content.substring(0, maxLength) + "...";
        }
        return content;
    }
}