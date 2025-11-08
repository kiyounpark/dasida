package com.bonju.review.quiz.service.register;

import com.bonju.review.knowledge.entity.Knowledge;

import com.bonju.review.quiz.client.AiClient;
import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.quiz.exception.errorcode.QuizErrorCode;
import com.bonju.review.quiz.exception.exception.QuizException;
import com.bonju.review.quiz.mapper.QuizGenerationMapper;
import com.bonju.review.quiz.repository.QuizRepository;
import com.bonju.review.quiz.vo.ImageResource;
import com.bonju.review.quiz.vo.QuizCreationData;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import com.bonju.review.util.ContentTypeExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class QuizAutoGeneratorServiceImpl implements QuizAutoGeneratorService {

  private final AiClient aiClient;
  private final QuizGenerationMapper quizGenerationMapper;
  private final QuizRepository quizRepository;
  private final UserService userService;

  @Override
  @Transactional
  public List<QuizCreationData> generateQuiz(Knowledge knowledge) {
    // Knowledge 엔티티에서 URL 리스트를 받아 사용한다
    List<ImageResource> imageResources = toImageResources(knowledge.getImageUrls());

    String rawJson = aiClient.generateRawQuizJson(knowledge.getText(), imageResources);
    List<QuizCreationData> creationDataList = quizGenerationMapper.mapFrom(rawJson);

    try {
      quizRepository.saveAll(mapToQuizEntities(creationDataList, knowledge));
    } catch (DataAccessException e) {
      throw new QuizException(QuizErrorCode.QUIZ_SAVE_FAILED, e);
    }

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
                    .build())
            .toList();
  }

  /**
   * 이미지 URL 목록을 ImageResource 목록으로 변환한다.
   * 입력이 null 또는 빈 경우 빈 리스트를 반환한다.
   */
  private static List<ImageResource> toImageResources(List<String> imageUrls) {
    if (imageUrls == null || imageUrls.isEmpty()) {
      return Collections.emptyList();
    }

    return imageUrls.stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(src -> new ImageResource(src, ContentTypeExtractor.extractMimeType(src)))
            .toList();
  }
}


