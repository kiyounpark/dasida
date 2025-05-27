package com.bonju.review.quiz.service;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.quiz.client.AiClient;
import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.quiz.mapper.QuizGenerationMapper;
import com.bonju.review.quiz.repository.QuizAutoGenerationRepository;
import com.bonju.review.quiz.vo.ImageResource;
import com.bonju.review.quiz.vo.QuizCreationData;
import com.bonju.review.user.service.UserService;
import com.bonju.review.user.entity.User;
import com.bonju.review.util.ContentTypeExtractor;
import com.bonju.review.util.HtmlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizAutoGeneratorServiceImpl implements QuizAutoGeneratorService {

  private final AiClient aiClient;
  private final QuizGenerationMapper quizGenerationMapper;
  private final QuizAutoGenerationRepository quizAutoGenerationRepository;
  private final UserService userService;

  @Override
  public List<QuizCreationData> generateQuiz(Knowledge knowledge, String content) {
    List<ImageResource> imageResources = extractImageResources(content);
    String rawJson = aiClient.generateRawQuizJson(content, imageResources);
    List<QuizCreationData> creationDataList = quizGenerationMapper.mapFrom(rawJson);

    quizAutoGenerationRepository.saveAll(
            mapToQuizEntities(creationDataList, knowledge)
    );

    return creationDataList;
  }

  private List<Quiz> mapToQuizEntities(List<QuizCreationData> creationDataList, Knowledge knowledge) {
    User user = userService.findUser();
    return creationDataList.stream()
            .map(data -> Quiz.builder()
                    .user(user)
                    .knowledge(knowledge)
                    .question(data.question())
                    .answer(data.answer())
                    .hint(data.hint())
                    .build()
            )
            .toList();
  }

  private static List<ImageResource> extractImageResources(String content) {
    return HtmlUtils.extractImageSrcList(content).stream()
            .map(src -> new ImageResource(src, ContentTypeExtractor.extractMimeType(src)))
            .toList();
  }
}
