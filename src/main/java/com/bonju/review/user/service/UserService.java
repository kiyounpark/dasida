package com.bonju.review.user.service;

import com.bonju.review.config.DemoUserService;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.exception.UserErrorCode;
import com.bonju.review.user.exception.UserException;
import com.bonju.review.user.repository.UserRepository;
import com.bonju.review.user.vo.KakaoUser;
import com.bonju.review.user.helper.AuthenticationHelper;
import com.bonju.review.util.auth.AuthErrorCode;
import com.bonju.review.util.auth.UnauthenticatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String DEMO_KAKAO_ID_PREFIX = "demo-";

    private final UserRepository userRepository;

    @Autowired(required = false)  // dev 프로필에서만 존재하므로 optional
    private DemoUserService demoUserService;

    /** 로그인 사용자 필수인 경우 사용 */
    @Transactional
    public User findUser() {
        String kakaoId = AuthenticationHelper.getKaKaoId();

        if (isDemoUser(kakaoId)) {
            return findOrCreateDemoUser(kakaoId);
        }

        return findRegularUser(kakaoId);
    }

    private boolean isDemoUser(String kakaoId) {
        return demoUserService != null && kakaoId.startsWith(DEMO_KAKAO_ID_PREFIX);
    }

    private User findOrCreateDemoUser(String kakaoId) {
        return demoUserService.findOrCreateDemoUser(kakaoId);
    }

    private User findRegularUser(String kakaoId) {
        return userRepository.findByKaKaoId(kakaoId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public User findUser(String kakaoId) {
        return userRepository.findByKaKaoId(kakaoId)
            .orElseThrow(() -> new UnauthenticatedException(AuthErrorCode.USER_NOT_FOUND));
    }

    /** 카카오 프로필을 기반으로 사용자 신규 생성 또는 닉네임 갱신 */
    @Transactional
    public void upsertUserByKakaoId(KakaoUser kakaoUser) {
        String kakaoId = requireNonNull(kakaoUser.id(), "kakaoId is null");
        String nickname = kakaoUser.properties().nickname();

        userRepository.findByKaKaoId(kakaoId)
                .ifPresentOrElse(
                        user -> user.updateUserNickname(nickname),
                        () -> createNewUser(kakaoId, nickname));
    }

    private void createNewUser(String kakaoId, String nickname) {
        userRepository.save(User.builder()
                .kakaoId(kakaoId)
                .nickname(nickname)
                .build());
    }
}
