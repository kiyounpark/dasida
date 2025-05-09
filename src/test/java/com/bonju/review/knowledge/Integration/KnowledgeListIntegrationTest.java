package com.bonju.review.knowledge.Integration;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.testsupport.security.WithMockKakaoUser;
import com.bonju.review.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class KnowledgeListIntegrationTest {

  private static final String KAKAO_ID = "123";
  private static final LocalDateTime FIXED_CREATED_AT = LocalDateTime.of(2025, 5, 9, 0, 0);

  private static final String API = "/knowledge";
  private static final int DEFAULT_OFFSET = 0;
  @Autowired MockMvc mockMvc;
  @Autowired EntityManager em;

  @BeforeEach
  void setUp() {
    User user = User.builder()
            .kakaoId(KAKAO_ID)
            .nickname("통합테스트유저")
            .build();
    em.persist(user);

    IntStream.range(1, 4).forEach(i -> em.persist(
            Knowledge.builder()
                    .user(user)
                    .title("지식 " + i)
                    .content("내용 " + i)
                    .createdAt(FIXED_CREATED_AT)
                    .build()
    ));
    em.flush();
  }

  @DisplayName("로그인 된 유저가 지식을 요청하면, 지식ListDto를 가져온다.")
  @WithMockKakaoUser(kakaoId = KAKAO_ID)   // 커스텀 애노테이션
  @Test
  void should_return_knowledge_list_for_authenticated_user() throws Exception {
    mockMvc.perform(get(API).param("offset", String.valueOf(DEFAULT_OFFSET)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.knowledge_list.length()").value(3))
            .andExpect(jsonPath("$.knowledge_list[0].title").value("지식 1"))
            .andExpect(jsonPath("$.page.nextOffset").doesNotExist());

  }

  @DisplayName("인증 없이 요청하면 401 Unauthorized")
  @Test
  void when_noAuthentication_then_unauthorized() throws Exception {
    mockMvc.perform(get(API).param("offset", String.valueOf(DEFAULT_OFFSET)))
            .andExpect(status().isUnauthorized());
  }

  @DisplayName("등록되지 않은 카카오 유저는 401 Unauthorized")
  @WithMockKakaoUser(kakaoId = "unknown")  // DB에 없는 ID
  @Test
  void when_userNotPersisted_then_unauthorized() throws Exception {
    mockMvc.perform(get(API).param("offset", String.valueOf(DEFAULT_OFFSET)))
            .andExpect(status().isUnauthorized());
  }

  @DisplayName("Offset 파라미터가 음수면 400 Bad Request")
  @WithMockKakaoUser(kakaoId = KAKAO_ID)
  @Test
  void when_invalidOffset_then_badRequest() throws Exception {
    mockMvc.perform(get(API).param("offset", "-1"))
            .andExpect(status().isBadRequest());
  }
}
