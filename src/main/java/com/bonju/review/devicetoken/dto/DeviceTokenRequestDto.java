package com.bonju.review.devicetoken.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * POST /device-token 요청 본문
 * <pre>
 * {
 *   "token": "sample-token"
 * }
 * </pre>
 */
public record DeviceTokenRequestDto(
        @NotBlank String token
) {}