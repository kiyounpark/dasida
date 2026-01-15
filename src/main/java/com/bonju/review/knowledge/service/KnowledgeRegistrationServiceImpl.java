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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KnowledgeRegistrationServiceImpl implements KnowledgeRegistrationService {

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

    // 디버그 로그
    System.out.println("====== findOrCreateUser DEBUG ======");
    System.out.println("kakaoId: " + kakaoId);
    System.out.println("demoUserService is null: " + (demoUserService == null));
    System.out.println("====================================");

    // YouTube 시연용: demo- 프리픽스인 경우 (DevApiKeyFilter에서 설정)
    if (demoUserService != null && kakaoId.startsWith("demo-")) {
      System.out.println(">>> Using DemoUserService");
      return demoUserService.findOrCreateDemoUser(kakaoId);
    }

    // 일반 개발자: 카카오 로그인 사용자 조회
    System.out.println(">>> Using UserService");
    return userService.findUser();
  }
}
