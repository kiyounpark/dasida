package com.bonju.review.user.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RememberMeConfig {

  private final PersistentTokenRepository tokenRepository;
  private final UserDetailsService userDetailsService;
  @Value("${security.remember-me.key}") String key;
  @Bean
  public RememberMeServices rememberMeServices() {
    final int VALIDITY_SEC = (int) Duration.ofDays(30).toSeconds(); // 30일

    PersistentTokenBasedRememberMeServices rememberMeServices =
            new PersistentTokenBasedRememberMeServices(key, userDetailsService, tokenRepository);

    rememberMeServices.setAlwaysRemember(true);
    rememberMeServices.setTokenValiditySeconds(VALIDITY_SEC);
    rememberMeServices.setCookieDomain("dasida.org");
    rememberMeServices.setUseSecureCookie(true);
    return rememberMeServices;
  }
}
