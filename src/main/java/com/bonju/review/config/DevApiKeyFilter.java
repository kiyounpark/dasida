package com.bonju.review.config;

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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("dev")  // 개발 환경에서만 활성화
public class DevApiKeyFilter extends OncePerRequestFilter {

    private static final String DEV_API_KEY = "dasida-dev-demo-key-2024";
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final int MAX_REQUESTS_PER_IP = 3;

    // IP별 요청 횟수 저장 (간단한 인메모리 저장소)
    private final Map<String, Integer> ipRequestCount = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {
        String requestPath = request.getRequestURI();

        // /knowledge POST 요청인 경우만 제한 (조회/이미지는 제한 없음)
        if ("POST".equals(request.getMethod()) && requestPath.startsWith("/knowledge")) {
            String clientIp = getClientIp(request);
            String apiKey = request.getHeader(API_KEY_HEADER);

            // API Key 검증
            if (!DEV_API_KEY.equals(apiKey)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
                return;
            }

            // IP별 요청 횟수 체크
            int count = ipRequestCount.getOrDefault(clientIp, 0);
            if (count >= MAX_REQUESTS_PER_IP) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write("{\"message\": \"지식 등록은 최대 3회까지만 가능합니다.\"}");
                return;
            }

            // 요청 횟수 증가
            ipRequestCount.put(clientIp, count + 1);

            // 임시 인증 정보 설정
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("demo-user", null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContextHolder.getContext().setAuthentication(auth);

        } else if (requestPath.startsWith("/knowledge") || requestPath.startsWith("/image")) {
            // GET /knowledge 또는 /image 엔드포인트는 API Key만 체크
            String apiKey = request.getHeader(API_KEY_HEADER);
            if (DEV_API_KEY.equals(apiKey)) {
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken("demo-user", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 클라이언트 IP 주소 추출 (프록시 고려)
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 여러 IP가 있는 경우 첫 번째 IP 사용
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
