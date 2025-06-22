package com.bonju.review.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class FcmConfig {

  /** service-account 경로를 application.yml 에서 주입 */
  @Value("${firebase.credentials}")
  private Resource credentials;

  @Bean
  public FirebaseApp firebaseApp() throws IOException {
    try (var in = credentials.getInputStream()) {
      FirebaseOptions opts = FirebaseOptions.builder()
              .setCredentials(GoogleCredentials.fromStream(in))
              .build();
      return FirebaseApp.initializeApp(opts);
    }
  }

  @Bean
  public FirebaseMessaging firebaseMessaging(FirebaseApp app) {
    return FirebaseMessaging.getInstance(app);
  }
}