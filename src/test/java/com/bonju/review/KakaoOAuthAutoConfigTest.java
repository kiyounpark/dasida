package com.bonju.review;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class KakaoOAuthAutoConfigTest {

    @Autowired
    OAuth2ClientProperties oAuth2ClientProperties;

    @Test
    @DisplayName("OAuth2ClientProperties를 통해 Kakao registration 정보가 잘 로드되는지 확인한다.")
    void testKakaoOAuth2PropertiesLoad() {
        // given
        OAuth2ClientProperties.Registration kakaoRegistration
                = oAuth2ClientProperties.getRegistration().get("kakao");

        // when
        String clientId      = kakaoRegistration.getClientId();
        String clientName    = kakaoRegistration.getClientName();
        String redirectUri   = kakaoRegistration.getRedirectUri();
        String authGrantType = kakaoRegistration.getAuthorizationGrantType();
        // scope 정보도 getScope()로 확인 가능

        // then
        assertThat(clientId).isEqualTo("test-client-id");
        assertThat(clientName).isEqualTo("Kakao");
        assertThat(redirectUri).isEqualTo("https://dev.api.dasida.org/login/oauth2/code/kakao");
        assertThat(authGrantType).isEqualTo("authorization_code");
    }
}
