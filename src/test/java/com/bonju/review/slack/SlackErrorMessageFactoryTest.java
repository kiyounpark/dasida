/* ---------- test/SlackErrorMessageFactoryTest.java ---------- */
package com.bonju.review.slack;

import com.bonju.review.util.TimestampProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;

class SlackErrorMessageFactoryTest {

  @Test
  @DisplayName("Clock 주입으로 고정된 타임스탬프를 반환한다")
  void timestamp_is_fixed_by_injected_clock() {
    // given : 2025-04-28 15:30:00(KST) 고정
    ZoneId zone   = ZoneId.of("Asia/Seoul");
    Clock   clock = Clock.fixed(
            LocalDateTime.of(2025, 4, 28, 15, 30, 0)
                    .atZone(zone).toInstant(), zone);

    TimestampProvider provider = new TimestampProvider(clock);

    // when
    String ts = provider.formatNow();

    // then
    assertThat(ts).isEqualTo("2025-04-28 15:30:00");
  }
}
