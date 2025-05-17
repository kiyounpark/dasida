package com.bonju.review.image.controller;

import com.bonju.review.image.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class S3Controller {
    private final S3Service s3Service;

    // ✅ 파일 업로드 후 JSON 형태로 URL 반환
    @PostMapping
    public Map<String, String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileUrl = s3Service.uploadAndGetUrl(file);
        return Map.of("imageUrl", fileUrl); // JSON 형식 반환
    }


    // ✅ 파일 삭제
    @DeleteMapping
    public String deleteFile(@RequestParam("fileName") String fileName) {
        s3Service.deleteFile(fileName);
        return "파일 삭제 완료: " + fileName;
    }


}
