package com.bonju.review.quiz.service;

import com.bonju.review.quiz.client.AiClient;
import com.bonju.review.quiz.mapper.QuizGenerationMapper;
import com.bonju.review.quiz.vo.QuizCreationData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizAutoGeneratorServiceImpl implements QuizAutoGeneratorService {

  private final AiClient aiClient;

  private final QuizGenerationMapper quizGenerationMapper;

  @Override
  public List<QuizCreationData> generateQuiz(String content) {
    String rawJson = aiClient.generateRawQuizJson(content);
    return quizGenerationMapper.mapFrom(rawJson);
  }
}
