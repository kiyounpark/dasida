package com.bonju.review.user.service;

import com.bonju.review.user.entity.User;
import com.bonju.review.user.exception.UserErrorCode;
import com.bonju.review.user.exception.UserException;
import com.bonju.review.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;   // Kakao-ID 기반 조회용 JPA 리포지토리

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String kakaoId) throws UsernameNotFoundException {
    // ① 사용자 조회 ─ kakaoId가 식별자
    User user = userRepository.findByKaKaoId(kakaoId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    // ② User → UserDetails 변환
    return org.springframework.security.core.userdetails.User.builder()
            .username(user.getKakaoId())       // 식별자
            .password("")                      // OAuth2 전용 → 비밀번호 없음
            .authorities("ROLE_USER")          // 기본 권한; 필요 시 DB 필드로 교체
            .disabled(false)                   // 추가 상태 필드가 있다면 반영
            .build();
  }
}