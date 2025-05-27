package com.bonju.review.quiz.client;


import com.bonju.review.quiz.exception.QuizErrorCode;
import com.bonju.review.quiz.exception.QuizException;
import com.bonju.review.quiz.vo.ImageResource;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenAiClient implements AiClient {

  private final ChatClient chatClient;

  @Override
  public String generateRawQuizJson(String content, List<ImageResource> images) {
    try {
      return chatClient.prompt()
              .user(userSpec -> {
                userSpec.text(content);
                for (ImageResource image : images) {
                  try {
                    userSpec.media(
                            image.mimeType(),
                            new UrlResource(image.url())
                    );
                  } catch (MalformedURLException e) {
                    throw new QuizException(QuizErrorCode.QUIZ_GENERATION_FAILED, e);
                  }
                }
              })
              .call()
              .content();
    } catch (Exception e) {
      throw new QuizException(QuizErrorCode.QUIZ_GENERATION_FAILED, e);
    }
  }
}
