package com.bonju.review.extractor;

import java.util.List;

public interface ImageExtractor {
    List<String> extractImageSrc(String html);
}
