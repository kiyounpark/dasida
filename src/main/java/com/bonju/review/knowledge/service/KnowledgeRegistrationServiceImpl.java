package com.bonju.review.knowledge.service;

import com.bonju.review.config.DemoUserService;
import com.bonju.review.knowledge.dto.KnowledgeRegisterRequestDto;
import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.exception.KnowledgeException;
import com.bonju.review.knowledge.mapper.KnowledgeMapper;
import com.bonju.review.knowledge.repository.KnowledgeRegistrationRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.helper.AuthenticationHelper;
import com.bonju.review.user.service.UserService;
import com.bonju.review.util.enums.error_code.KnowledgeErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeRegistrationServiceImpl implements KnowledgeRegistrationService {

  private static final String DEMO_KAKAO_ID_PREFIX = "demo-";

  private final KnowledgeRegistrationRepository repository;
  private final UserService userService;
  private final KnowledgeMapper knowledgeMapper;

  @Autowired(required = false)  // dev 프로필에서만 존재하므로 optional
  private DemoUserService demoUserService;

  @Override
  @Transactional
  public Knowledge registerKnowledge(KnowledgeRegisterRequestDto knowledgeRegisterRequestDto) {
    try {
      User user = findOrCreateUser();
      Knowledge knowledge = knowledgeMapper.toEntity(user, knowledgeRegisterRequestDto);
      repository.save(knowledge);
      return knowledge;
    } catch (DataAccessException e) {
      throw new KnowledgeException(KnowledgeErrorCode.REGISTER_FAILED, e);
    }
  }

  /**
   * 사용자를 조회하거나 생성
   *
   * - YouTube 시연 모드: demo- 프리픽스 사용자 자동 생성 (API Key 인증)
   * - 일반 개발 모드: 기존 사용자 조회만 (카카오 OAuth2 인증)
   */
  private User findOrCreateUser() {
    String kakaoId = AuthenticationHelper.getKaKaoId();

    log.debug("====== findOrCreateUser DEBUG ======");
    log.debug("kakaoId: {}", kakaoId);
    log.debug("demoUserService is null: {}", demoUserService == null);
    log.debug("====================================");

    if (isDemoUser(kakaoId)) {
      log.debug(">>> Using DemoUserService");
      return demoUserService.findOrCreateDemoUser(kakaoId);
    }

    log.debug(">>> Using UserService");
    return userService.findUser();
  }

  private boolean isDemoUser(String kakaoId) {
    return demoUserService != null && kakaoId.startsWith(DEMO_KAKAO_ID_PREFIX);
  }
}
