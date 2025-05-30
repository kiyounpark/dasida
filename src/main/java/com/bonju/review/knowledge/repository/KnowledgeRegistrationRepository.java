package com.bonju.review.knowledge.repository;

import com.bonju.review.knowledge.entity.Knowledge;

public interface KnowledgeRegistrationRepository {
  Knowledge save(Knowledge knowledge);
}
