package com.bonju.review.quiz.client;

import com.bonju.review.exception.exception.OpenAiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.net.MalformedURLException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j // SLF4J 로거 사용
public class OpenAiClient1 implements AiClient1 {
    private final ChatClient chatClient;

    @Override
    public String getQuizJson(String prompt, List<String> imageUrls) {
        log.info("OpenAI API 호출 시작: prompt={}", prompt); // API 호출 시작 로깅
        String jsonResponse = createQuiz(prompt, imageUrls);
        log.info("OpenAI API 호출 성공: 응답 데이터 크기={} bytes", jsonResponse.length()); // API 호출 성공 로깅
        return jsonResponse;
    }

    public String createQuiz(String prompt, List<String> imageUrls) {
        try {
            log.debug("ChatClient 호출 시작: prompt={}", prompt);
            String response = chatClient.prompt()
                    .user(userSpec -> {
                        // HTML 프롬프트 설정
                        userSpec.text(prompt);
                        // imageUrls 리스트에 값이 없으면 forEach는 실행되지 않습니다.
                        imageUrls.forEach(url -> {
                            try {
                                userSpec.media(MimeTypeUtils.IMAGE_JPEG, new UrlResource(url));
                            } catch (MalformedURLException e) {
                                log.error("잘못된 이미지 URL: {}", url, e);
                            }
                        });
                    })
                    .call()
                    .content();
            log.debug("ChatClient 호출 성공: 응답={}", response);
            return response;
        } catch (OpenAiException e) {
            log.error("OpenAI API 호출 실패: prompt={}, 에러 메시지={}", prompt, e.getMessage(), e);
            throw new OpenAiException("OpenAI API가 응답을 반환하는데 실패하였습니다.");
        }
    }
}
