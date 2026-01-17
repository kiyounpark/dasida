package com.bonju.review.config;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IP별 요청 제한을 관리하는 서비스
 * YouTube 시연 환경에서 지식 등록 요청을 IP당 최대 3회로 제한
 */
@Service
public class RateLimitService {

    private static final int MAX_REQUESTS_PER_IP = 3;
    private final Map<String, Integer> ipRequestCount = new ConcurrentHashMap<>();

    /**
     * IP의 요청 가능 여부 확인
     * @param ip 클라이언트 IP 주소
     * @return true면 허용, false면 차단
     */
    public boolean isAllowed(String ip) {
        int count = ipRequestCount.getOrDefault(ip, 0);
        return count < MAX_REQUESTS_PER_IP;
    }

    /**
     * IP의 요청 횟수 증가
     * @param ip 클라이언트 IP 주소
     */
    public void incrementCount(String ip) {
        int count = ipRequestCount.getOrDefault(ip, 0);
        ipRequestCount.put(ip, count + 1);
    }

    /**
     * 현재 IP의 요청 횟수 조회
     * @param ip 클라이언트 IP 주소
     * @return 현재까지의 요청 횟수
     */
    public int getCount(String ip) {
        return ipRequestCount.getOrDefault(ip, 0);
    }

    /**
     * 모든 IP의 카운트 초기화 (테스트용)
     */
    public void reset() {
        ipRequestCount.clear();
    }
}
