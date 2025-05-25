package com.bonju.review.image.controller;

import com.bonju.review.image.dto.ImageResponseDto;
import com.bonju.review.image.exception.errorcode.ImageErrorCode;
import com.bonju.review.image.exception.errorcode.StorageErrorCode;
import com.bonju.review.image.exception.exception.ImageException;
import com.bonju.review.image.exception.exception.StorageException;
import com.bonju.review.image.service.ImageService;
import com.bonju.review.slack.SlackErrorMessageFactory;
import com.bonju.review.util.RestApiTestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
class ImageControllerTest {

  private static final String IMAGE_ENDPOINT = "/image";
  private static final String FIELD_FILE = "file";
  private static final String VALID_FILENAME = "sample.jpg";
  private static final String INVALID_FILENAME = "sample.gif";
  private static final byte[] FILE_CONTENT = "dummy".getBytes();
  private static final String EXPECTED_URL =
          "https://my-bucket.s3.ap-northeast-2.amazonaws.com/images/sample.jpg";

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @MockitoBean ImageService imageService;
  @MockitoBean SlackErrorMessageFactory slackErrorMessageFactory;

  @Test
  @DisplayName("이미지를 업로드하면 public URL DTO를 반환한다")
  @WithMockUser
  void uploadImage_returnsPublicUrlDto() throws Exception {
    // given
    given(imageService.uploadAndGetPublicUrl(any()))
            .willReturn(new ImageResponseDto(EXPECTED_URL));

    MockMultipartFile file = new MockMultipartFile(
            FIELD_FILE, VALID_FILENAME, MediaType.IMAGE_JPEG_VALUE, FILE_CONTENT);

    // when
    String json = mockMvc.perform(multipart(IMAGE_ENDPOINT).file(file).with(csrf()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // then
    assertThat(objectMapper.readValue(json, ImageResponseDto.class).imageUrl())
            .isEqualTo(EXPECTED_URL);
  }

  @Nested
  @DisplayName("지원하지 않는 파일 형식")
  class UnsupportedFileType {

    @Test
    @DisplayName("확장자가 허용 목록에 없으면 INVALID_EXTENSION 코드와 400을 반환한다")
    @WithMockUser
    void returnsBadRequest_whenUnsupportedExtension() throws Exception {
      // given
      given(imageService.uploadAndGetPublicUrl(any()))
              .willThrow(new ImageException(ImageErrorCode.INVALID_EXTENSION));

      MockMultipartFile file = new MockMultipartFile(
              FIELD_FILE, INVALID_FILENAME, MediaType.IMAGE_GIF_VALUE, FILE_CONTENT);

      // when
      String body = mockMvc.perform(multipart(IMAGE_ENDPOINT).file(file).with(csrf()))
              .andExpect(status().isBadRequest())
              .andReturn()
              .getResponse()
              .getContentAsString();

      // then
      RestApiTestUtils.assertError(body, ImageErrorCode.INVALID_EXTENSION, IMAGE_ENDPOINT);
    }

    @Test
    @DisplayName("MIME 타입이 허용 목록에 없으면 INVALID_MIME_TYPE 코드와 400을 반환한다")
    @WithMockUser
    void returnsBadRequest_whenUnsupportedMimeType() throws Exception {
      // given
      given(imageService.uploadAndGetPublicUrl(any()))
              .willThrow(new ImageException(ImageErrorCode.INVALID_MIME_TYPE));

      MockMultipartFile file = new MockMultipartFile(
              FIELD_FILE, INVALID_FILENAME, MediaType.IMAGE_GIF_VALUE, FILE_CONTENT);

      // when
      String body = mockMvc.perform(multipart(IMAGE_ENDPOINT).file(file).with(csrf()))
              .andExpect(status().isBadRequest())
              .andReturn()
              .getResponse()
              .getContentAsString();

      // then
      RestApiTestUtils.assertError(body, ImageErrorCode.INVALID_MIME_TYPE, IMAGE_ENDPOINT);
    }
  }

  @Nested
  @DisplayName("스토리지 업로드 실패")
  class StorageFailure {

    @Test
    @DisplayName("StorageException 이 발생하면 500(Internal Server Error)과 UPLOAD_FAILED 코드가 반환된다")
    @WithMockUser
    void returnsInternalServerError_whenStorageFails() throws Exception {
      // given
      given(imageService.uploadAndGetPublicUrl(any()))
              .willThrow(new StorageException(StorageErrorCode.UPLOAD_FAILED)); // ✅ 스토리지 예외

      MockMultipartFile file = new MockMultipartFile(
              FIELD_FILE, VALID_FILENAME, MediaType.IMAGE_JPEG_VALUE, FILE_CONTENT);

      // when
      String body = mockMvc.perform(multipart(IMAGE_ENDPOINT).file(file).with(csrf()))
              .andExpect(status().isInternalServerError())
              .andReturn()
              .getResponse()
              .getContentAsString();

      verify(slackErrorMessageFactory).createErrorMessage(any(), any());
      RestApiTestUtils.assertError(body, StorageErrorCode.UPLOAD_FAILED, IMAGE_ENDPOINT);
    }
  }

}
