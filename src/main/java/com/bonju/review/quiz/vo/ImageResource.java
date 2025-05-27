package com.bonju.review.quiz.vo;

import org.springframework.util.MimeType;
public record ImageResource(String url, MimeType mimeType) {
}
