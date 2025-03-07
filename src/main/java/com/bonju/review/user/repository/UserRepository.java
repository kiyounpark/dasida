package com.bonju.review.user.repository;

import com.bonju.review.user.entity.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByKaKaoId(String kakaoId);

    User save(User user);
}
