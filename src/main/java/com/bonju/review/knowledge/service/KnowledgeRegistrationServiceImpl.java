package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.knowledge.repository.KnowledgeRegistrationRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import com.bonju.review.util.enums.error_code.KnowledgeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class KnowledgeRegistrationServiceImpl implements KnowledgeRegistrationService {

  private final KnowledgeRegistrationRepository repository;
  private final UserService userService;

  @Override
  @Transactional
  public Knowledge registerKnowledge(String title, String content) {
    try {
      User user = userService.findUser();
      Knowledge knowledge = Knowledge.builder()
              .user(user)
              .title(title)
              .text(content)
              .createdAt(LocalDateTime.now())
              .build();
      repository.save(knowledge);
      return knowledge;
    } catch (DataAccessException e) {
      throw new KnowledgeException(KnowledgeErrorCode.REGISTER_FAILED, e);
    }
  }
}
