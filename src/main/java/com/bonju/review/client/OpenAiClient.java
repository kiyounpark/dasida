package com.bonju.review.client;

import com.bonju.review.dto.QuizDto;
import com.bonju.review.dto.QuizWrapperDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenAiClient {
    private final ChatClient chatClient;

    /**
     * OpenAI API를 호출하여 퀴즈 JSON 데이터를 가져옴
     *
     * @param prompt 사용자가 입력한 프롬프트
     * @return JSON 문자열 데이터
     */
    private String getQuizJson(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }



    /**
     * JSON 데이터를 QuizWrapperDto로 변환 후, 내부 List<QuizDto> 반환
     *
     * @param jsonData JSON 데이터
     * @return 변환된 List<QuizDto>
     */
    private List<QuizDto> parseJsonToQuizList(String jsonData) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        try {
            QuizWrapperDto quizWrapper = objectMapper.readValue(jsonData, QuizWrapperDto.class);
            return quizWrapper.getQuizzes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON to QuizWrapperDto", e);
        }
    }

    /**
     * OpenAI API를 호출하고 JSON 데이터를 파싱하여 List<QuizDto>로 변환
     *
     * @param prompt 사용자가 입력한 프롬프트
     * @return 변환된 List<QuizDto>
     */
    public List<QuizDto> generateQuizList(String prompt) {
        String jsonData = getQuizJson(prompt); // JSON 데이터 가져오기
        return parseJsonToQuizList(jsonData); // JSON -> List<QuizDto> 변환
    }
}
