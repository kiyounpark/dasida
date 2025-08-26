package com.bonju.review.devicetoken.repository;

import com.bonju.review.devicetoken.entity.DeviceToken;
import com.bonju.review.user.entity.User;

import java.util.Optional;

public interface DeviceTokenRepository {

  Optional<DeviceToken> findByUser(User user);
  Optional<DeviceToken> findByUserIdAndToken(User user, String token);

  DeviceToken save(DeviceToken token);

  void deleteByToken(String token);
}
