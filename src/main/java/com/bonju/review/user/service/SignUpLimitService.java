package com.bonju.review.user.service;

import com.bonju.review.user.dto.SignUpAvailabilityResponseDto;
import com.bonju.review.user.exception.UserErrorCode;
import com.bonju.review.user.exception.UserException;
import com.bonju.review.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpLimitService {

  private final UserRepository userRepository;

  public static final int MAX_USER_COUNT = 1000;

  @Transactional(readOnly = true)
  public SignUpAvailabilityResponseDto checkSignUpAvailability() {
    try {
      long current = userRepository.count();          // 예외 발생 가능 지점
      return new SignUpAvailabilityResponseDto(current < MAX_USER_COUNT);
    } catch (DataAccessException e) {
      // 로깅 · 모니터링 후 도메인 예외로 변환
      throw new UserException(UserErrorCode.COUNT_FAIL, e);
    }
  }
}
