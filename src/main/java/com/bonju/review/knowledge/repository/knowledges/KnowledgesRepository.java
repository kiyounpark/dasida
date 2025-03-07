package com.bonju.review.knowledge.repository.knowledges;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.user.entity.User;

import java.util.List;

public interface KnowledgesRepository {
    public List<Knowledge> findKnowledgesByDaysAgo(User user, int days);
}
