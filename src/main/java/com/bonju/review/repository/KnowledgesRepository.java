package com.bonju.review.repository;

import com.bonju.review.entity.Knowledge;
import com.bonju.review.entity.User;

import java.util.List;

public interface KnowledgesRepository {
    public List<Knowledge> findKnowledgesByDaysAgo(User user, int days);
}
