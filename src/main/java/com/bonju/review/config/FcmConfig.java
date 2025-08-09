package com.bonju.review.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
@Slf4j
public class FcmConfig {

  /** service-account 경로를 application.yml 에서 주입 */
  @Value("${firebase.credentials}")
  private Resource credentials;

  // FcmConfig.java (@Slf4j)
  @Bean
  public FirebaseApp firebaseApp() throws IOException {
    if (FirebaseApp.getApps().isEmpty()) {
      try (var in = credentials.getInputStream()) {
        FirebaseOptions opts = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(in))
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(opts);
        log.info("[FCM] Firebase projectId={}", app.getOptions().getProjectId());
        return app;
      }
    }
    FirebaseApp app = FirebaseApp.getInstance();
    log.info("[FCM] Firebase projectId={}", app.getOptions().getProjectId());
    return app;
  }


  @Bean
  public FirebaseMessaging firebaseMessaging(FirebaseApp app) {
    return FirebaseMessaging.getInstance(app);
  }
}