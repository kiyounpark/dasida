package com.bonju.review;

import com.bonju.review.user.entrypoint.JsonAuthenticationEntryPoint;
import com.bonju.review.user.handler.CustomOAuth2AuthenticationFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;


@Configuration
@EnableWebSecurity
@Profile("test")
public class SecurityConfig {

    @Autowired
    JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;

    @Autowired
    AuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                // 여기 추가 👇
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint)
                )
                .oauth2Login(
                        oauth2 -> oauth2.failureHandler(authenticationFailureHandler)
                );


        return http.build();
    }
}


