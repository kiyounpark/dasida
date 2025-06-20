/* ────────────────── DeviceTokenController (void 반환) ────────────────── */
package com.bonju.review.devicetoken.controller;

import com.bonju.review.devicetoken.dto.DeviceTokenRequestDto;
import com.bonju.review.devicetoken.service.DeviceTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeviceTokenController {

  private final DeviceTokenService deviceTokenService;

  @PostMapping("/token")
  ResponseEntity<Void> register(@RequestBody @Valid DeviceTokenRequestDto request) {
    deviceTokenService.registerDeviceToken(request.token());
    return ResponseEntity.ok().build();        // 200, 본문 없음
  }
}