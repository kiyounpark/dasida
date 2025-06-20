package com.bonju.review.devicetoken.integration;

import com.bonju.review.devicetoken.dto.DeviceTokenRequestDto;
import com.bonju.review.devicetoken.entity.DeviceToken;
import com.bonju.review.testsupport.security.WithMockKakaoUser;
import com.bonju.review.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 통합 테스트 (Mockito 없이 실제 빈·DB 사용)
 * ▸ 검증: 200 OK 반환 + DeviceToken 저장 여부
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class DeviceTokenIntegrationTest {

  private static final String END_POINT = "/token";
  private static final String TOKEN     = "AAA.BBB.CCC";

  @Autowired MockMvc      mockMvc;
  @Autowired ObjectMapper objectMapper;
  @Autowired EntityManager em;

  @Test
  @DisplayName("POST /token → 200 OK, 토큰이 DB에 저장된다")
  @WithMockKakaoUser(kakaoId = "123")
  void register_returns200_andPersistsToken() throws Exception {
    /* ─── given ─── */
    User user = User.builder()
            .kakaoId("123")
            .nickname("tester")
            .build();
    em.persist(user);
    em.flush();

    DeviceTokenRequestDto request = new DeviceTokenRequestDto(TOKEN);

    /* ─── when & then ─── */
    mockMvc.perform(post(END_POINT)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

    DeviceToken saved = em.createQuery(
                    "select dt from DeviceToken dt where dt.token = :token",
                    DeviceToken.class)
            .setParameter("token", TOKEN)
            .getSingleResult();

    assertThat(saved.getUser().getKakaoId()).isEqualTo("123");
    assertThat(saved.getToken()).isEqualTo(TOKEN);
  }
}