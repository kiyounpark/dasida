package com.bonju.review.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // ✅ dasida.dev와 api.dasida.dev 허용 (서브도메인 포함)
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",               // 로컬 개발 (모든 포트)
                "http://127.0.0.1:*",               // 로컬 개발 (IP)
                "https://dev.dasida.org:3000",      // 프론트엔드
                "https://dasida.org",               // 백엔드 API
                "https://dasida-youtube.netlify.app" // Netlify 배포
        ));
        // 요청 메서드 허용 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 모든 헤더 허용
        configuration.setAllowedHeaders(List.of("*"));
        // 세션 쿠키 등 자격증명(credential)을 함께 전달할 수 있도록 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
