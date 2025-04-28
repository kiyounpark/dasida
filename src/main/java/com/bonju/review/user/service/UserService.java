package com.bonju.review.user.service;

import com.bonju.review.user.entity.User;
import com.bonju.review.user.repository.UserRepository;
import com.bonju.review.user.vo.KakaoUser;
import com.bonju.review.user.helper.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /** 로그인 사용자 필수인 경우 사용 */
    @Transactional(readOnly = true)
    public User findUser() {
        Optional<User> userByKakaoId = userRepository.findByKaKaoId(AuthenticationHelper.getKaKaoId());
        return userByKakaoId.orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
    }

    /** 카카오 프로필을 기반으로 사용자 신규 생성 또는 닉네임 갱신 */
    @Transactional
    public void upsertUserByKakaoId(KakaoUser kakaoUser) {
        String kakaoId = requireNonNull(kakaoUser.id(), "kakaoId is null");
        String nickname = kakaoUser.properties().nickname();

        userRepository.findByKaKaoId(kakaoId)
                .ifPresentOrElse(
                        user -> user.updateUserNickname(nickname),
                        () -> userRepository.save(User.builder()
                                .kakaoId(kakaoId)
                                .nickname(nickname)
                                .build()));
    }



}
