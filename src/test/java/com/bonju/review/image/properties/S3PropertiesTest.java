package com.bonju.review.image.properties;

import com.bonju.review.image.config.S3Properties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        S3PropertiesTest.PROP_ACCESS_KEY,
        S3PropertiesTest.PROP_SECRET_KEY,
        S3PropertiesTest.PROP_BUCKET,
        S3PropertiesTest.PROP_REGION
})
@EnableConfigurationProperties(S3Properties.class)
class S3PropertiesTest {

  private static final String EXPECTED_BUCKET = "test-bucket";
  private static final String EXPECTED_REGION = "ap-northeast-2";
  private static final String EXPECTED_ACCESS_KEY = "access-key";
  private static final String EXPECTED_SECRET_KEY = "secret-key";

  public static final String PROP_ACCESS_KEY = "s3.access-key=" + EXPECTED_ACCESS_KEY;
  public static final String PROP_SECRET_KEY = "s3.secret-key=" + EXPECTED_SECRET_KEY;
  public static final String PROP_BUCKET = "s3.bucket=" + EXPECTED_BUCKET;
  public static final String PROP_REGION = "s3.region=" + EXPECTED_REGION;

  @Autowired
  S3Properties s3Properties;

  @DisplayName("S3 설정값이 제대로 들어오나 확인한다.")
  @Test
  void properties_are_bound_correctly() {
    // given
    String bucket = s3Properties.getBucket();
    String region = s3Properties.getRegion();
    String accessKey = s3Properties.getAccessKey();
    String secretKey = s3Properties.getSecretKey();

    // then
    assertThat(bucket).isEqualTo(EXPECTED_BUCKET);
    assertThat(region).isEqualTo(EXPECTED_REGION);
    assertThat(accessKey).isEqualTo(EXPECTED_ACCESS_KEY);
    assertThat(secretKey).isEqualTo(EXPECTED_SECRET_KEY);
  }
}
