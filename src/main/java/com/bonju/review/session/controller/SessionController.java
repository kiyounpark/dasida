package com.bonju.review.session.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {
  @GetMapping("/session-check")
  public ResponseEntity<Void> sessionCheck(Authentication auth) {
    if (auth == null || !auth.isAuthenticated()) {
      return ResponseEntity.status(401).build();
    }
    return ResponseEntity.noContent().build();
  }
}