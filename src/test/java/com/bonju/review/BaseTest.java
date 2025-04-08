package com.bonju.review;

import com.bonju.review.knowledge.converter.MarkdownConverter;
import com.bonju.review.knowledge.repository.knowledges.KnowledgesRepository;
import com.bonju.review.knowledge.repository.register.KnowledgeRegisterRepository;
import com.bonju.review.knowledge.service.register.KnowledgeRegisterService;
import com.bonju.review.user.repository.UserRepository;
import com.bonju.review.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected OAuth2ClientProperties oAuth2ClientProperties;

    @MockitoBean
    protected OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected MarkdownConverter markdownConverter;

    @Autowired
    protected KnowledgeRegisterService knowledgeRegisterService;

    @Autowired
    protected KnowledgesRepository knowledgesRepository;

    @Autowired
    protected KnowledgeRegisterRepository knowledgeRegisterRepository;

    @Autowired
    protected EntityManager entityManager;

}
