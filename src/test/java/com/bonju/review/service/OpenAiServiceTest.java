package com.bonju.review.service;

import com.bonju.review.dto.KnowledgeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpenAiServiceTest {

    @Autowired OpenAiService openAiService;

    @Test
    public void 테스트(){
        //given
        KnowledgeDto knowledgeDto = new KnowledgeDto("안녕 클레오 파트라", "세상에서 제일 가는 포테이토칩");

        // when
        openAiService.saveQuiz(knowledgeDto);

        // then

    }
}