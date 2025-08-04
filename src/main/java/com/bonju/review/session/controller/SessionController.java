package com.bonju.review.session.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {
  @GetMapping("/session-check")
  public ResponseEntity<Void> sessionCheck(Authentication auth) {
    if (auth == null || !auth.isAuthenticated()) {
      return ResponseEntity.status(401)
              .cacheControl(CacheControl.noStore())
              .header("Pragma", "no-cache")
              .header("Expires", "0")
              .header("Vary", "Cookie, Authorization")
              .build();
    }
    return ResponseEntity.noContent()
            .cacheControl(CacheControl.noStore())
            .header("Pragma", "no-cache")
            .header("Expires", "0")
            .header("Vary", "Cookie, Authorization")
            .build();
  }
}