package com.bonju.review.config;

import com.bonju.review.user.entity.User;
import com.bonju.review.user.helper.AuthenticationHelper;
import com.bonju.review.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * YouTube 시연용 데모 사용자 서비스
 * demo- 프리픽스를 가진 임시 사용자를 자동으로 생성하고 관리
 * DevApiKeyFilter에서 API Key 인증 시 활성화됨
 */
@Service
@Profile("dev")
@RequiredArgsConstructor
public class DemoUserService {

    private static final String DEMO_KAKAO_ID_PREFIX = "demo-";
    private static final String DEMO_NICKNAME_PREFIX = "데모유저-";
    private static final int DEMO_PREFIX_LENGTH = 5; // "demo-".length()

    private final UserRepository userRepository;

    /**
     * demo- 프리픽스 사용자를 조회하거나 없으면 자동 생성
     * IP별로 독립적인 임시 사용자를 생성하여 데이터 격리 제공
     *
     * @param kakaoId demo- 프리픽스가 포함된 kakaoId (예: demo-192-168-1-100)
     * @return 조회되거나 새로 생성된 User 엔티티
     */
    @Transactional
    public User findOrCreateDemoUser(String kakaoId) {
        return userRepository.findByKaKaoId(kakaoId)
            .orElseGet(() -> createDemoUser(kakaoId));
    }

    /**
     * demo 사용자를 새로 생성
     *
     * @param kakaoId demo- 프리픽스가 포함된 kakaoId
     * @return 새로 생성된 User 엔티티
     */
    private User createDemoUser(String kakaoId) {
        String ipInfo = extractIpInfo(kakaoId);
        String nickname = createDemoNickname(ipInfo);

        User demoUser = User.builder()
            .kakaoId(kakaoId)
            .nickname(nickname)
            .build();

        return userRepository.save(demoUser);
    }

    private String extractIpInfo(String kakaoId) {
        return kakaoId.substring(DEMO_PREFIX_LENGTH);
    }

    private String createDemoNickname(String ipInfo) {
        return DEMO_NICKNAME_PREFIX + ipInfo;
    }

    /**
     * 현재 인증된 사용자가 demo 사용자인지 확인
     *
     * @return demo 사용자이면 true, 아니면 false
     */
    public boolean isDemoUser() {
        try {
            String kakaoId = AuthenticationHelper.getKaKaoId();
            return kakaoId.startsWith(DEMO_KAKAO_ID_PREFIX);
        } catch (Exception e) {
            return false;
        }
    }
}
