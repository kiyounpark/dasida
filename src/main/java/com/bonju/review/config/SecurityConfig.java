package com.bonju.review.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationFailureHandler failureHandler;
    private final AuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 요청 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        // OAuth2 로그인 처리용 URL은 모두 허용
                        .requestMatchers("/oauth2/authorization/**").permitAll()
                        // 그 외 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // 2. formLogin을 비활성화함 -> 401 에러 반환이 가능해짐
                .formLogin(Customizer.withDefaults())

//                // 3. 인증되지 않은 사용자 접근 시 (세션 쿠키 없는 경우 포함), 401 에러 반환
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
//                        })
//                )

                // 4. OAuth2 로그인은 그대로 유지(선택 사항)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                );

        return http.build();
    }
}
