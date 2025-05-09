package com.bonju.review.knowledge.repository;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.user.entity.User;

import java.util.List;

public interface KnowledgeListRepository {
  List<Knowledge> findKnowledgeList(User user, int offset, int limit);
}
