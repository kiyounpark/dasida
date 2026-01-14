package com.bonju.review.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * HTTP 요청에서 실제 클라이언트 IP 주소를 추출하는 유틸리티
 * 프록시, 로드밸런서 환경을 고려하여 다양한 헤더를 확인
 */
@Component
public class IpExtractor {

    /**
     * 요청에서 실제 클라이언트 IP 추출
     * 프록시 환경을 고려하여 다음 순서로 확인:
     * 1. X-Forwarded-For 헤더 (표준)
     * 2. Proxy-Client-IP 헤더
     * 3. WL-Proxy-Client-IP 헤더 (WebLogic)
     * 4. request.getRemoteAddr() (직접 연결)
     *
     * @param request HTTP 요청 객체
     * @return 클라이언트 IP 주소
     */
    public String getClientIp(HttpServletRequest request) {
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
        // 여러 IP가 있는 경우 첫 번째 IP 사용 (실제 클라이언트)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
