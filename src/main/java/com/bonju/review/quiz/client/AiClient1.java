package com.bonju.review.quiz.client;

import java.util.List;

public interface AiClient1 {
    String getQuizJson(String prompt, List<String> imageUrls);
}
