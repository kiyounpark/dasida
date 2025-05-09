package com.bonju.review.knowledge.repository;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.user.entity.User;

public interface KnowledgeReadRepository {

  Knowledge findKnowledge(User user, Long id);
}
