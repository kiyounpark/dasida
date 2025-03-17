package com.bonju.review;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;


class KakaoOAuthFlowTest extends BaseTest {

    @Test
    @DisplayName("카카오 로그인 URL(/oauth2/authorization/kakao) 호출 시 카카오 인증 페이지로 리다이렉트되어야 한다.")
    void shouldRedirectToKakaoAuthorizationServer() throws Exception {
        // given
        String kakaoLoginUrl = "/oauth2/authorization/kakao";  // 요청할 URL
        String expectedRedirectUrl = "kauth.kakao.com/oauth/authorize";  // 기대하는 리다이렉트 주소

        // when & then
        mockMvc.perform(get(kakaoLoginUrl))
                .andExpect(status().is3xxRedirection())  // 3xx 리다이렉트 상태 확인
                .andExpect(header().string("Location", containsString(expectedRedirectUrl)));  // 리다이렉트 URL 확인
    }

}
