package com.bonju.review.knowledge.service;

import com.bonju.review.knowledge.dto.KnowledgeRegisterRequestDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.knowledge.mapper.KnowledgeMapper;
import com.bonju.review.knowledge.repository.KnowledgeRegistrationRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import com.bonju.review.util.enums.error_code.KnowledgeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KnowledgeRegistrationServiceImpl implements KnowledgeRegistrationService {

  private final KnowledgeRegistrationRepository repository;
  private final UserService userService;
  private final KnowledgeMapper knowledgeMapper;

  @Override
  @Transactional
  public Knowledge registerKnowledge(KnowledgeRegisterRequestDto knowledgeRegisterRequestDto) {
    try {
      User user = userService.findUser();
      Knowledge knowledge = knowledgeMapper.toEntity(user, knowledgeRegisterRequestDto);
      repository.save(knowledge);
      return knowledge;
    } catch (DataAccessException e) {
      throw new KnowledgeException(KnowledgeErrorCode.REGISTER_FAILED, e);
    }
  }
}
