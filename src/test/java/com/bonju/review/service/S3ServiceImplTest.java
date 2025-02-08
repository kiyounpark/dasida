package com.bonju.review.service;

import com.bonju.review.S3Uploader;
import com.bonju.review.S3UrlProvider;
import com.bonju.review.dto.ImageRequestDto;
import com.bonju.review.exception.exception.S3UploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.s3.model.S3Exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {

    @Mock
    S3UrlProvider s3UrlProvider;

    @Mock
    S3Uploader s3Uploader;
    @InjectMocks
    S3ServiceImpl s3Service;

    private ImageRequestDto imageRequestDto;

    private MockMultipartFile createMockFile() {
        // 512KB(= 524,288 bytes) 크기의 더미 데이터 생성
        byte[] halfMBData = new byte[524288];

        // 파일 생성
        return new MockMultipartFile(
                "file",                          // 파라미터 이름
                "test-halfmb.jpg",               // 파일 이름
                "image/jpeg",                    // MIME 타입 (예: 이미지)
                halfMBData                        // 파일 데이터
        );
    }

    @BeforeEach
    void setUp(){
        MockMultipartFile mockFile = createMockFile();
        imageRequestDto = new ImageRequestDto(mockFile);
    }

    @Test
    @DisplayName("🟢 정상적인 이미지 업로드 시 S3 URL 반환")
    void givenValidFile_whenGetImageUrl_thenReturnS3Url(){
        //given
        String expectedUrl = "https://s3.ap-northeast-2.amazonaws.com/my-bucket/uploads/2025/02/03/abc123456789.jpg";
        doNothing().when(s3Uploader).uploadImage(imageRequestDto.getImage());
        when(s3UrlProvider.createImageUrl(imageRequestDto.getImage())).thenReturn(expectedUrl); // 반환값 설정

        //when
        String imageUrl = s3Service.getImageUrl(imageRequestDto);

        //then
        assertEquals(expectedUrl, imageUrl);
        verify(s3Uploader, times(1)).uploadImage(imageRequestDto.getImage());
        verify(s3UrlProvider, times(1)).createImageUrl(imageRequestDto.getImage());
    }

    @Test
    @DisplayName("❌ S3 업로드 실패 시 S3UploadException 발생")
    void givenInvalidFile_whenGetImageUrl_thenThrowS3UploadException(){
        //given
        // S3Service에서 S3Exception이 발생하도록 Mock 설정
        String errorMessage = "S3 업로드 실패";
        doThrow(new S3UploadException(errorMessage)).when(s3Uploader).uploadImage(imageRequestDto.getImage());

        //when & then
        S3UploadException s3UploadException = assertThrows(S3UploadException.class, () -> s3Service.getImageUrl(imageRequestDto));

        assertAll(
                () -> assertNotNull(s3UploadException),  // 예외가 실제 발생했는지 확인
                () -> assertEquals(errorMessage, s3UploadException.getMessage()) // 예외 메시지가 예상과 같은지 확인
        );

        verify(s3Uploader, times(1)).uploadImage(imageRequestDto.getImage());
        verify(s3UrlProvider, times(0)).createImageUrl(imageRequestDto.getImage());
    }
}