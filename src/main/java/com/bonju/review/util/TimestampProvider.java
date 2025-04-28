/* ---------- util/TimestampProvider.java ---------- */
package com.bonju.review.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** ❶ Clock 주입형 -– 테스트·운영 모두 한 곳에서 관리 */
@Component
@RequiredArgsConstructor
public class TimestampProvider {

  private static final DateTimeFormatter FORMATTER =
          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final Clock clock;

  public String formatNow() {                            // 시각 포맷 반환
    return LocalDateTime.now(clock).format(FORMATTER);
  }
}
