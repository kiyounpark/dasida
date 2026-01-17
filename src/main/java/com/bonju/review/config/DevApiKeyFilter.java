package com.bonju.review.config;

import com.bonju.review.util.IpExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * 개발 환경 전용 API Key 인증 필터
 * YouTube 시연을 위해 간단한 API Key 기반 인증을 제공
 */
@Component
@Profile("dev")  // 개발 환경에서만 활성화
@Slf4j
public class DevApiKeyFilter extends OncePerRequestFilter {

    private static final String DEV_API_KEY = "dasida-dev-demo-key-2024";
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String DEMO_KAKAO_ID_PREFIX = "demo-";
    private static final String DEMO_NICKNAME_PREFIX = "데모유저-";
    private static final String KNOWLEDGE_PATH = "/knowledge";
    private static final String IMAGE_PATH = "/image";
    private static final String HOME_PATH = "/home";
    private static final String QUIZZES_PATH = "/quizzes";
    private static final String HTTP_METHOD_POST = "POST";
    private static final String RATE_LIMIT_ERROR_MESSAGE = "{\"message\": \"지식 등록은 최대 3회까지만 가능합니다.\"}";
    private static final String OAUTH_PROVIDER_KAKAO = "kakao";
    private static final String IP_SEPARATOR = ".";
    private static final String KAKAO_ID_SEPARATOR = "-";

    private final RateLimitService rateLimitService;
    private final IpExtractor ipExtractor;

    public DevApiKeyFilter(RateLimitService rateLimitService, IpExtractor ipExtractor) {
        this.rateLimitService = rateLimitService;
        this.ipExtractor = ipExtractor;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {
        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        log.debug("====== DevApiKeyFilter 진입 ======");
        log.debug("요청 경로: {} {}", method, requestPath);

        if (isKnowledgePostRequest(method, requestPath)) {
            handleKnowledgePostRequest(request, response);
        } else if (isQuizPostRequest(method, requestPath)) {
            handleQuizPostRequest(request, response);
        } else if (isReadOnlyRequest(requestPath)) {
            handleReadOnlyRequest(request);
        } else {
            log.debug("→ DevApiKeyFilter 대상 아님, 패스");
        }

        log.debug("====== DevApiKeyFilter 종료 ======");
        filterChain.doFilter(request, response);
    }

    private boolean isKnowledgePostRequest(String method, String requestPath) {
        return HTTP_METHOD_POST.equals(method) && requestPath.startsWith(KNOWLEDGE_PATH);
    }

    private boolean isReadOnlyRequest(String requestPath) {
        return requestPath.startsWith(KNOWLEDGE_PATH)
            || requestPath.startsWith(IMAGE_PATH)
            || requestPath.startsWith(HOME_PATH);
    }

    private boolean isQuizPostRequest(String method, String requestPath) {
        return HTTP_METHOD_POST.equals(method) && requestPath.startsWith(QUIZZES_PATH);
    }

    private void handleKnowledgePostRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("→ POST /knowledge 요청 감지");

        String clientIp = ipExtractor.getClientIp(request);
        String apiKey = request.getHeader(API_KEY_HEADER);

        log.debug("clientIp: {}", clientIp);
        log.debug("API Key: {}", apiKey != null ? "존재함" : "없음");

        if (isInvalidApiKey(apiKey)) {
            log.debug("✗ API Key 불일치 - 401 반환");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
            return;
        }
        log.debug("✓ API Key 검증 통과");

        if (isRateLimitExceeded(clientIp)) {
            log.debug("✗ Rate Limit 초과 - 403 반환");
            sendRateLimitError(response);
            return;
        }
        log.debug("✓ Rate Limit 통과");

        rateLimitService.incrementCount(clientIp);
        setAuthentication(clientIp);
        log.debug("✓ 인증 정보 설정 완료");
    }

    private void handleReadOnlyRequest(HttpServletRequest request) {
        log.debug("→ GET 요청 또는 이미지 요청");

        String apiKey = request.getHeader(API_KEY_HEADER);
        if (isValidApiKey(apiKey)) {
            String clientIp = ipExtractor.getClientIp(request);
            setAuthentication(clientIp);
            log.debug("✓ 인증 정보 설정 완료");
        } else {
            log.debug("→ API Key 없음, 인증 설정 스킵");
        }
    }

    private void handleQuizPostRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("→ POST /quizzes 요청 감지");

        String apiKey = request.getHeader(API_KEY_HEADER);

        log.debug("API Key: {}", apiKey != null ? "존재함" : "없음");

        if (isInvalidApiKey(apiKey)) {
            log.debug("✗ API Key 불일치 - 401 반환");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
            return;
        }
        log.debug("✓ API Key 검증 통과");

        String clientIp = ipExtractor.getClientIp(request);
        setAuthentication(clientIp);
        log.debug("✓ 인증 정보 설정 완료");
    }

    private boolean isInvalidApiKey(String apiKey) {
        return !DEV_API_KEY.equals(apiKey);
    }

    private boolean isValidApiKey(String apiKey) {
        return DEV_API_KEY.equals(apiKey);
    }

    private boolean isRateLimitExceeded(String clientIp) {
        return !rateLimitService.isAllowed(clientIp);
    }

    private void sendRateLimitError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(RATE_LIMIT_ERROR_MESSAGE);
    }

    /**
     * IP 기반 OAuth2User로 인증 정보 설정
     * 각 IP마다 고유한 임시 사용자를 생성하여 데이터 격리 제공
     *
     * @param clientIp 클라이언트 IP 주소
     */
    private void setAuthentication(String clientIp) {
        String demoKakaoId = createDemoKakaoId(clientIp);
        String demoNickname = createDemoNickname(clientIp);

        log.debug("====== DevApiKeyFilter.setAuthentication ======");
        log.debug("clientIp: {}", clientIp);
        log.debug("demoKakaoId: {}", demoKakaoId);
        log.debug("==============================================");

        OAuth2User oauth2User = createOAuth2User(demoKakaoId, demoNickname);
        OAuth2AuthenticationToken auth = createAuthenticationToken(oauth2User);

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String createDemoKakaoId(String clientIp) {
        return DEMO_KAKAO_ID_PREFIX + clientIp.replace(IP_SEPARATOR, KAKAO_ID_SEPARATOR);
    }

    private String createDemoNickname(String clientIp) {
        return DEMO_NICKNAME_PREFIX + clientIp;
    }

    private OAuth2User createOAuth2User(String demoKakaoId, String demoNickname) {
        Map<String, Object> attributes = Map.of(
            "id", demoKakaoId,
            "properties", Map.of("nickname", demoNickname)
        );

        return new DefaultOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            "id"
        );
    }

    private OAuth2AuthenticationToken createAuthenticationToken(OAuth2User oauth2User) {
        return new OAuth2AuthenticationToken(
            oauth2User,
            oauth2User.getAuthorities(),
            OAUTH_PROVIDER_KAKAO
        );
    }
}
