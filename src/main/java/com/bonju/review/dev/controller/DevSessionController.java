package com.bonju.review.dev.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
public class DevSessionController {

  @GetMapping("/_kill-session")
  public void killSession(HttpServletRequest req) {
    var session = req.getSession(false);
    if (session != null) session.invalidate();
  }
}