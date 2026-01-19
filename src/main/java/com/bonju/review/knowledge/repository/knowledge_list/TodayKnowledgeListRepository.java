package com.bonju.review.knowledge.repository.knowledge_list;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.vo.SingleDayRange;
import com.bonju.review.user.entity.User;

import java.util.List;

public interface TodayKnowledgeListRepository {
    List<Knowledge> findKnowledgeListByDateRange(User user, SingleDayRange dayRange);

    /**
     * 유튜브 시연용: 날짜 제한 없이 사용자의 모든 지식 조회
     */
    List<Knowledge> findAllKnowledges(User user);
}
