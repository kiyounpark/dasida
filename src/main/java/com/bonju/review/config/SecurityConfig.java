package com.bonju.review.config;

import com.bonju.review.user.service.CustomOAuth2UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthenticationFailureHandler failureHandler;
  private final AuthenticationSuccessHandler successHandler;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final RememberMeServices rememberMeServices;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .securityContext(ctx -> ctx.requireExplicitSave(false))
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/health", "/login-enabled", "/h2-console/**",
                            "/oauth2/authorization/**", "/slack-test", "/slack-test/**",
                            "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**")
                    .permitAll()
                    .anyRequest().authenticated()
            )
            .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
              res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              res.setContentType("application/json");
              res.getWriter().write("{\"message\": \"로그인이 필요합니다.\"}");
            }))
            .rememberMe(rem -> rem.rememberMeServices(rememberMeServices)
            )
            .oauth2Login(oauth2 -> oauth2.successHandler(successHandler).failureHandler(failureHandler));

    return http.build();
  }
}