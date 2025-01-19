package com.bonju.review.repository;

import com.bonju.review.client.OpenAiClient;
import com.bonju.review.dto.QuizDto;
import com.bonju.review.entity.Knowledge;
import com.bonju.review.entity.Quiz;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class OpenAiRepositoryJpa {

    private final EntityManager em;
    private final OpenAiClient client;

    @Transactional
    public void createQuiz() {
        // 1. Knowledge 생성
        Knowledge kiyoonpark = new Knowledge("박기윤의 정보", "박기윤의 나이는 28이고 여자친구 이름은 김원희이다.");

        // 2. Knowledge를 영속화 (DB에 저장)
        em.persist(kiyoonpark);

        // 3. OpenAiClient를 통해 퀴즈 생성
        List<QuizDto> quizDtos = client.generateQuizList(kiyoonpark.getDescription());

        // 4. QuizDto 데이터를 기반으로 Quiz 엔티티 생성 및 영속화
        for (QuizDto quizDto : quizDtos) {
            Quiz quiz = new Quiz(kiyoonpark, quizDto.getQuiz(), quizDto.getAnswer(), quizDto.getCommentary());
            em.persist(quiz);
        }

        // 5. 트랜잭션이 끝나면 EntityManager가 변경 내용을 DB에 반영
    }
}

