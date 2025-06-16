package com.bonju.review.useranswer.repository;

import com.bonju.review.user.entity.User;
import com.bonju.review.useranswer.entity.UserAnswer;

import java.util.List;

public interface UserAnswerRepository {
  void save(UserAnswer userAnswer);

  List<UserAnswer> findAll(User user);
}
