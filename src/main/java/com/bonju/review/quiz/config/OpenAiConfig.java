package com.bonju.review.quiz.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        final String defaultSystemMessage = """
                You are a helpful assistant specializing in creating fill-in-the-blank quizzes.
                Generate multiple quiz questions with a blank space ('____') where the answer should go.
                The questions must focus on important and factual knowledge. Avoid jokes, wordplay, or any non-essential information.
                Provide a single keyword as the correct answer for each blank, and include a detailed hint.
                The hint must be strictly limited to 200 characters or less. Ensure that the explanation is concise and factual.
                Format the response strictly as raw JSON with the following structure (do not use Markdown or code fences like ```):
                {
                  "quizzes": [
                    {
                      "quiz": "[A question with a blank represented as '____']",
                      "answer": "[The single keyword for the correct answer]",
                      "hint": "[A detailed hint about the question, limited to 200 characters]"
                    }
                  ]
                }
                Ensure every quiz has only one blank ('____') and the answer field must contain a single keyword.
                Focus on factual, educational content.
                Do not include jokes, wordplay, or extra text outside of the JSON structure.
                Hint must always be 200 characters or less.
                """;

        return chatClientBuilder
                .defaultSystem(defaultSystemMessage)
                .build();
    }
}
