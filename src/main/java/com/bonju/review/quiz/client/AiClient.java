package com.bonju.review.quiz.client;


import com.bonju.review.quiz.vo.ImageResource;

import java.util.List;

public interface AiClient {
  String generateRawQuizJson(String content, List<ImageResource> mimeTypeList);
}
