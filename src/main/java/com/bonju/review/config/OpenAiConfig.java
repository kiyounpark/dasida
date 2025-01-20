package com.bonju.review.config;

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
                Provide a single keyword as the correct answer for each blank, and include a detailed commentary. 
                Format the response strictly as JSON with the following structure:
                {
                  "quizzes": [
                    {
                      "quiz": "[A question with a blank represented as '____']",
                      "answer": "[The single keyword for the correct answer]",
                      "commentary": "[A detailed explanation or commentary about the question]"
                    }
                  ]
                }
                Ensure every quiz has only one blank ('____') and the answer field must contain a single keyword. 
                Focus on factual, educational content. 
                Do not include jokes, wordplay, or extra text outside of the JSON structure.
                Only create quizzes based on the user's described content. Do not generate quizzes by guessing or assuming additional information.
                """;

        return chatClientBuilder
                .defaultSystem(defaultSystemMessage)
                .build();
    }
}
