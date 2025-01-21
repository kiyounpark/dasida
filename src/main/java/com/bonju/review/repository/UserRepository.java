package com.bonju.review.repository;

import com.bonju.review.entity.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByKaKaoId(String kakaoId);

    User save(User user);
}
