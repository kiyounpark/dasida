package com.bonju.review.devicetoken.service;

import com.bonju.review.devicetoken.entity.DeviceToken;
import com.bonju.review.devicetoken.exception.DeviceTokenErrorCode;
import com.bonju.review.devicetoken.exception.DeviceTokenException;
import com.bonju.review.devicetoken.repository.DeviceTokenRepository;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceTokenService {

  private final UserService userService;
  private final DeviceTokenRepository deviceTokenRepository;

  @Transactional
  public void registerDeviceToken(String token){
    User user = userService.findUser();

    DeviceToken deviceToken = DeviceToken.builder()
            .user(user)
            .token(token)
            .build();

    try{
      deviceTokenRepository.save(deviceToken);
    }
    catch (DataAccessException e){
     throw new DeviceTokenException(DeviceTokenErrorCode.DB_FAIL, e);
    }
  }

  @Transactional(readOnly = true)
  public Optional<DeviceToken> findOptionalDeviceToken(User user) {
    try {
      return deviceTokenRepository.findByUser(user);
    } catch (DataAccessException e) {
      log.error("토큰 조회 DB 오류", e);
      return Optional.empty();
    }
  }

  @Transactional
  public void deleteByToken(String token) {
    try {
      deviceTokenRepository.deleteByToken(token);
    } catch (DataAccessException e) {
      log.error("토큰 삭제 DB 오류", e);
    }
  }
}