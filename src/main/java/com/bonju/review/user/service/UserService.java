package com.bonju.review.user.service;

import com.bonju.review.user.helper.AuthenticationHelper;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.repository.UserRepository;
import com.bonju.review.user.vo.KakaoUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUser() {
        String kakaoId = AuthenticationHelper.getKaKaoId();

        return userRepository.findByKaKaoId(kakaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public void upsertUserByKakaoId(KakaoUser kakaoUser) {
        String kakaoId = kakaoUser.id();
        String nickname = kakaoUser.properties().nickname();

        Optional<User> optionalUser = userRepository.findByKaKaoId(kakaoId);

        if (optionalUser.isEmpty()) {
            User newUser = User.builder()
                    .kakaoId(kakaoId)
                    .nickname(nickname).build();

            userRepository.save(newUser);
            return;
        }

        User existingUser = optionalUser.get();
        existingUser.updateUserNickname(nickname);
    }

}
