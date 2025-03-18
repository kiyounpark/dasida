package com.bonju.review;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OAuth2LoginFailureTest extends BaseTest {

    @Test
    @DisplayName("소셜 로그인 실패 상황에서 401 응답과 모든 ErrorResponse 필드 검증")
    void oauth2LoginFailureWithCompleteErrorResponseTest() throws Exception {
        // Given: OAuth2UserService가 소셜 로그인 실패 예외를 던지도록 설정
        given(oAuth2UserService.loadUser(ArgumentMatchers.any(OAuth2UserRequest.class)))
                .willThrow(new OAuth2AuthenticationException("소셜 로그인 실패"));

        // When & Then: /login/oauth2/code/kakao 엔드포인트 호출 시, 401 상태와 ErrorResponse의 모든 필드 검증
        mockMvc.perform(get("/login/oauth2/code/kakao"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("소셜 로그인 인증에 실패했습니다. 다시 시도해주세요."))
                .andExpect(jsonPath("$.path").value("/login/oauth2/code/kakao"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
