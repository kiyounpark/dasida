package com.bonju.review.quiz.client;

import com.bonju.review.quiz.exception.exception.QuizException;
import com.bonju.review.quiz.vo.ImageResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OpenAiClientTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  ChatClient chatClient;

  @InjectMocks
  OpenAiClient openAiClient;

  @Test
  @DisplayName("정상적으로 JSON 문자열을 반환한다")
  void returnsJsonString() {
    // given
    String expected =
        """
        {"question":"2+2?","answer":"4","hint":"basic math"}
        """;

    // prompt() → user(spec) → call() → content()
    given(chatClient.prompt()
            .user(ArgumentMatchers.<Consumer<ChatClient.PromptUserSpec>>any())
            .call()
            .content())
            .willReturn(expected);

    // when
    String result = openAiClient.generateRawQuizJson(
            "2+2?",
            List.of() // 이미지가 없으면 빈 리스트
    );

    // then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  @DisplayName("내부 오류 발생 시 AiException 으로 래핑한다")
  void wrapsToAiException() {
    // given
    RuntimeException cause = new RuntimeException("boom");

    given(chatClient.prompt()
            .user(ArgumentMatchers.<Consumer<ChatClient.PromptUserSpec>>any())
            .call()
            .content())
            .willThrow(cause);

    // when - then
    List<ImageResource> images = List.of(new ImageResource("url", null));

    QuizException ex = assertThrows(
            QuizException.class,
            () -> openAiClient.generateRawQuizJson("any content", images)
    );

    assertThat(ex.getCause()).isSameAs(cause);
  }
}
