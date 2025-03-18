package com.bonju.review.user.vo;

import static org.assertj.core.api.Assertions.assertThat;

import com.bonju.review.BaseTest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

class KakaoUserFromOAuth2UserTest extends BaseTest {

    @Test
    @DisplayName("OAuth2User의 attributes가 KaKaoUser 객체로 올바르게 변환되는지 검증한다.")
    void testConvertOAuth2UserAttributesToKaKaoUser() {
        // Given
        Map<String, Object> attributes = Map.of(
                "id", 100L,
                "connected_at", "2025-02-15T10:34:39Z",
                "properties", Map.of("nickname", "박기윤"),
                "kakao_account", Map.of(
                        "profile_nickname_needs_agreement", false,
                        "profile", Map.of(
                                "nickname", "박기윤",
                                "is_default_nickname", false
                        )
                )
        );
        OAuth2User oauth2User = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("OAUTH2_USER")),
                attributes,
                "id"
        );

        // When: OAuth2User의 attributes를 KaKaoUser 객체로 변환
        KakaoUser kaKaoUser = objectMapper.convertValue(oauth2User.getAttributes(), KakaoUser.class);

        // Then: 변환된 객체의 각 필드 검증
        assertThat(kaKaoUser).isNotNull();
        assertThat(kaKaoUser.id()).isEqualTo(100L);
        assertThat(kaKaoUser.connectedAt()).isEqualTo("2025-02-15T10:34:39Z");
        assertThat(kaKaoUser.properties()).isNotNull();
        assertThat(kaKaoUser.properties().nickname()).isEqualTo("박기윤");
    }
}