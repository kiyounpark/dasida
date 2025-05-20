package com.bonju.review.image.storage.provider;

import com.bonju.review.image.config.S3Properties;
import com.bonju.review.image.exception.errorcode.StorageErrorCode;
import com.bonju.review.image.exception.exception.StorageException;
import com.bonju.review.image.storage.objectkey.ObjectKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class S3ImagePublicUrlProviderTest {

  @Mock S3Client s3Client;
  @Mock S3Utilities s3Utilities;
  @Mock S3Properties s3Properties;

  @InjectMocks S3ImagePublicUrlProvider provider;

  private static final ObjectKey SAMPLE_KEY = new ObjectKey("images/sample.jpg");
  private static final String EXPECTED_URL_STR =
          "https://my-bucket.s3.ap-northeast-2.amazonaws.com/images/sample.jpg";

  /* ------------------------------------------------------------------
   * 1. 정상 경로
   * ------------------------------------------------------------------ */
  @Test
  @DisplayName("S3에서 Public URL을 정상적으로 생성한다")
  void getPublicUrl_returnsCorrectUrl() throws Exception {
    given(s3Properties.getBucket()).willReturn("my-bucket");
    given(s3Client.utilities()).willReturn(s3Utilities);
    given(s3Utilities.getUrl(any(GetUrlRequest.class)))
            .willReturn(new URL(EXPECTED_URL_STR));

    String actual = provider.getPublicUrl(SAMPLE_KEY);

    assertEquals(EXPECTED_URL_STR, actual);
  }

  /* ------------------------------------------------------------------
   * 2. AWS SDK 예외 → RETRIEVE_FAILED
   * ------------------------------------------------------------------ */
  @Test
  @DisplayName("AWS SDK 오류를 StorageException(RETRIEVE_FAILED)로 래핑한다")
  void getPublicUrl_wrapsSdkClientException() {
    given(s3Properties.getBucket()).willReturn("my-bucket");
    given(s3Client.utilities()).willReturn(s3Utilities);
    given(s3Utilities.getUrl(any(GetUrlRequest.class)))
            .willThrow(SdkClientException.builder().message("network down").build());

    assertThatThrownBy(() -> provider.getPublicUrl(SAMPLE_KEY))
            .isInstanceOf(StorageException.class)
            .hasFieldOrPropertyWithValue("errorCode", StorageErrorCode.RETRIEVE_FAILED);
  }

  /* ------------------------------------------------------------------
   * 3. getUrl()이 null 반환 → RETRIEVE_FAILED
   * ------------------------------------------------------------------ */
  @Test
  @DisplayName("S3가 null URL을 반환하면 StorageException(RETRIEVE_FAILED)을 던진다")
  void getPublicUrl_throwsWhenReturnedUrlIsNull() {
    given(s3Properties.getBucket()).willReturn("my-bucket");
    given(s3Client.utilities()).willReturn(s3Utilities);
    given(s3Utilities.getUrl(any(GetUrlRequest.class))).willReturn(null);

    assertThatThrownBy(() -> provider.getPublicUrl(SAMPLE_KEY))
            .isInstanceOf(StorageException.class)
            .hasFieldOrPropertyWithValue("errorCode", StorageErrorCode.RETRIEVE_FAILED);
  }
}
