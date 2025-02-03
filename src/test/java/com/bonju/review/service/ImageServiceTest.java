package com.bonju.review.service;

import com.bonju.review.dto.ImageRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {
    @Mock
    S3Service s3Service;

    @InjectMocks
    ImageServiceImpl imageService;

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

    @Test
    void givenValidFile_whenGetImageUrl_thenReturnS3Url(){
        //given
        MockMultipartFile mockFile = createMockFile();
        ImageRequestDto imageRequestDto = new ImageRequestDto(mockFile);
        String expectedUrl = "https://s3.ap-northeast-2.amazonaws.com/my-bucket/uploads/2025/02/03/abc123456789.jpg";
        when(s3Service.createImageUrl(imageRequestDto.getImage())).thenReturn(expectedUrl);

        //when
        String imageUrl = imageService.getImageUrl(imageRequestDto);

        //then
        assertEquals(expectedUrl, imageUrl);
        verify(s3Service, times(1)).createImageUrl(imageRequestDto.getImage());
    }
}