package com.bonju.review.config;

import com.bonju.review.util.IpExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

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

            // 임시 인증 정보 설정
            setAuthentication();

        } else if (requestPath.startsWith("/knowledge") || requestPath.startsWith("/image")) {
            // GET /knowledge 또는 /image 엔드포인트는 API Key만 체크
            String apiKey = request.getHeader(API_KEY_HEADER);
            if (DEV_API_KEY.equals(apiKey)) {
                setAuthentication();
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * demo-user로 인증 정보 설정
     */
    private void setAuthentication() {
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken("demo-user", null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
