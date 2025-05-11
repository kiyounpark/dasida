package com.bonju.review.knowledge.repository;

import com.bonju.review.knowledge.entity.Knowledge;

import java.util.List;

public interface KnowledgeSearchRepository {
  List<Knowledge> findByTitleContaining(String title);
}
