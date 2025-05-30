package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.knowledge.repository.KnowledgeRegistrationRepository;
import com.bonju.review.util.enums.error_code.KnowledgeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KnowledgeRegistrationServiceImpl implements KnowledgeRegistrationService {

  private final KnowledgeRegistrationRepository repository;

  @Override
  @Transactional
  public Knowledge registerKnowledge(String title, String content) {
    try {
      Knowledge knowledge = Knowledge.builder()
              .title(title)
              .content(content)
              .build();
      repository.save(knowledge);
      return knowledge;
    } catch (DataAccessException e) {
      throw new KnowledgeException(KnowledgeErrorCode.REGISTER_FAILED, e);
    }
  }
}
