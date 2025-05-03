package com.bonju.review.knowledge.repository.knowledge_list;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.vo.SingleDayRange;
import com.bonju.review.user.entity.User;

import java.util.List;

public interface TodayKnowledgeListRepository {
    List<Knowledge> findKnowledgeListByDateRange(User user, SingleDayRange dayRange);
}
