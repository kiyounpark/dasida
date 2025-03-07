package com.bonju.review.knowledge.extractor;

import java.util.List;

public interface ImageExtractor {
    List<String> extractImageSrc(String html);
}
