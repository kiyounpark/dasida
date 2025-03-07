package com.bonju.review.user.service;

import com.bonju.review.user.helper.AuthenticationHelper;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUser() {
        String kakaoId = AuthenticationHelper.getKaKaoId();

        return userRepository.findByKaKaoId(kakaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
    }
}
