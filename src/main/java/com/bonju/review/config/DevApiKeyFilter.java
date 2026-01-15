package com.bonju.review.config;

import com.bonju.review.util.IpExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
public class DevApiKeyFilter extends OncePerRequestFilter {

    private static final String DEV_API_KEY = "dasida-dev-demo-key-2024";
    private static final String API_KEY_HEADER = "X-API-Key";

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

        // /knowledge POST 요청인 경우만 제한 (조회/이미지는 제한 없음)
        if ("POST".equals(request.getMethod()) && requestPath.startsWith("/knowledge")) {
            String clientIp = ipExtractor.getClientIp(request);
            String apiKey = request.getHeader(API_KEY_HEADER);

            // API Key 검증
            if (!DEV_API_KEY.equals(apiKey)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
                return;
            }

            // IP별 요청 횟수 체크
            if (!rateLimitService.isAllowed(clientIp)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write("{\"message\": \"지식 등록은 최대 3회까지만 가능합니다.\"}");
                return;
            }

            // 요청 횟수 증가
            rateLimitService.incrementCount(clientIp);

            // IP 기반 임시 인증 정보 설정
            setAuthentication(clientIp);

        } else if (requestPath.startsWith("/knowledge") || requestPath.startsWith("/image")) {
            // GET /knowledge 또는 /image 엔드포인트는 API Key만 체크
            String apiKey = request.getHeader(API_KEY_HEADER);
            if (DEV_API_KEY.equals(apiKey)) {
                String clientIp = ipExtractor.getClientIp(request);
                setAuthentication(clientIp);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * IP 기반 OAuth2User로 인증 정보 설정
     * 각 IP마다 고유한 임시 사용자를 생성하여 데이터 격리 제공
     *
     * @param clientIp 클라이언트 IP 주소
     */
    private void setAuthentication(String clientIp) {
        // IP 기반 고유 kakaoId 생성 (예: "demo-192-168-1-100")
        String demoKakaoId = "demo-" + clientIp.replace(".", "-");
        String demoNickname = "데모유저-" + clientIp;

        // OAuth2User 형태로 인증 정보 생성 (AuthenticationHelper가 OAuth2User를 우선 처리)
        Map<String, Object> attributes = Map.of(
            "id", demoKakaoId,
            "properties", Map.of("nickname", demoNickname)
        );

        OAuth2User oauth2User = new DefaultOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            "id"
        );

        OAuth2AuthenticationToken auth = new OAuth2AuthenticationToken(
            oauth2User,
            oauth2User.getAuthorities(),
            "kakao"
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
