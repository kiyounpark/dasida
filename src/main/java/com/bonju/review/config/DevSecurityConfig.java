package com.bonju.review.config;

import com.bonju.review.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@RequiredArgsConstructor
@Profile("dev")
public class DevSecurityConfig {

    private final AuthenticationFailureHandler failureHandler;
    private final AuthenticationSuccessHandler successHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .cors(Customizer.withDefaults())
                // 1. 요청 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/oauth2/authorization/**").permitAll()
                        .requestMatchers(   "/swagger-ui/**",   // Swagger UI 경로
                                "/v3/api-docs/**",  // OpenAPI 문서 경로
                                "/swagger-resources/**",
                                "/webjars/**"       // Swagger 관련 정적 리소스
                                         ).permitAll()
                        .anyRequest().authenticated()
                )

                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                )

                // 3. 인증되지 않은 사용자 접근 시 (세션 쿠키 없는 경우 포함), 401 에러 반환
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
                        })
                )

                // 4. OAuth2 로그인은 그대로 유지(선택 사항)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                );

        return http.build();
    }
}
