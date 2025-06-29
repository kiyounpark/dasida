package com.bonju.review.user.controller;

import com.bonju.review.user.dto.SignUpAvailabilityResponseDto;
import com.bonju.review.user.service.SignUpLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 회원 가입 가능 여부(유저 수 1 000 명 제한) 조회 컨트롤러.
 *
 * <pre>
 *  GET /api/auth/login-enabled
 *  └─ body: { "canSignUp": true | false }
 * </pre>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/login-enabled")
public class SignUpAvailabilityController {

  private final SignUpLimitService signUpLimitService;

  /** 가입 가능 여부를 그대로 DTO 로 반환 */
  @GetMapping
  public SignUpAvailabilityResponseDto checkSignUpAvailability() {
    return signUpLimitService.checkSignUpAvailability();
  }
}
