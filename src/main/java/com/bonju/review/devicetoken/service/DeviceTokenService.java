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
}