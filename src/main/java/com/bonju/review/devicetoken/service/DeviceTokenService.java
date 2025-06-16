package com.bonju.review.devicetoken.service;

import com.bonju.review.devicetoken.entity.DeviceToken;
import com.bonju.review.devicetoken.exception.DeviceTokenErrorCode;
import com.bonju.review.devicetoken.exception.DeviceTokenException;
import com.bonju.review.devicetoken.repository.DeviceTokenRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeviceTokenService {

  private final UserService userService;
  private final DeviceTokenRepository deviceTokenRepository;

  /**
   * <pre>
   * 1. 현재 로그인 사용자를 조회한다.
   * 2. (user, token) 조합으로 토큰 엔티티가 존재하면 그대로 반환
   * 3. 없으면 새 엔티티를 생성‧저장 후 토큰 문자열 반환
   * </pre>
   */
  @Transactional
  public String getOrCreateToken(String token) {
    User user = userService.findUser();

    try{
      return deviceTokenRepository.findByUserIdAndToken(user, token)
              .map(DeviceToken::getToken)
              .orElseGet(() -> {
                DeviceToken saved = deviceTokenRepository.save(
                        DeviceToken.builder()
                                .user(user)
                                .token(token)
                                .build()
                );
                return saved.getToken();
              });
    }
    catch (DataAccessException e){
      throw new DeviceTokenException(DeviceTokenErrorCode.DB_FAIL, e);
    }
  }
}