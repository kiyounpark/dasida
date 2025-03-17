package com.bonju.review;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.equalTo;

class UnauthorizedAccessTest extends BaseTest {

    @Test
    @DisplayName("로그인하지 않은 사용자가 보호된 리소스 접근 시 401 에러와 JSON 응답 반환")
    void shouldReturn401WithJsonBodyWhenAccessingProtectedResourceWithoutLogin() throws Exception {
        // given
        String protectedUrl = "/api/protected";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get(protectedUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", equalTo(401)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", equalTo("Unauthorized")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", equalTo("로그인이 필요합니다.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", equalTo(protectedUrl)));
    }
}