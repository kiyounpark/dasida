package com.bonju.review.util;

import com.bonju.review.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public final class RestApiTestUtils {

  private static final ObjectMapper om =
          new ObjectMapper().findAndRegisterModules();

  private RestApiTestUtils() {}

  public static void assertError(String json,
                                 ApiErrorCode expected,
                                 String expectedPath) throws Exception {

    ErrorResponse actual = om.readValue(json, ErrorResponse.class);

    assertThat(actual.getMessage()).isEqualTo(expected.getMessage());
    assertThat(actual.getStatus()).isEqualTo(expected.getHttpStatus().value());
    assertThat(actual.getPath()).isEqualTo(expectedPath);
    assertThat(actual.getError())
            .isEqualTo(expected.getHttpStatus().getReasonPhrase());
  }
}
