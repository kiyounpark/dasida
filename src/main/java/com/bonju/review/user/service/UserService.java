package com.bonju.review.user.service;

import com.bonju.review.user.entity.User;
import com.bonju.review.user.exception.UserErrorCode;
import com.bonju.review.user.exception.UserException;
import com.bonju.review.user.repository.UserRepository;
import com.bonju.review.user.vo.KakaoUser;
import com.bonju.review.user.helper.AuthenticationHelper;
import com.bonju.review.util.auth.AuthErrorCode;
import com.bonju.review.util.auth.UnauthenticatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return userByKakaoId.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public User findUser(String kakaoId) {
        Optional<User> userByKakaoId = userRepository.findByKaKaoId(kakaoId);
        return userByKakaoId.orElseThrow(() -> new UnauthenticatedException(
                AuthErrorCode.USER_NOT_FOUND));
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
