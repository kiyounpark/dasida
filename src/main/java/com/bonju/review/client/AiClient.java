package com.bonju.review.client;

import java.util.List;

public interface AiClient {
    String getQuizJson(String prompt, List<String> imageUrls);
}
