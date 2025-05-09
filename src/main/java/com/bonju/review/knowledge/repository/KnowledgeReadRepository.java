package com.bonju.review.knowledge.repository;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.user.entity.User;

import java.util.Optional;

public interface KnowledgeReadRepository {

  Optional<Knowledge> findKnowledge(User user, Long id);
}
