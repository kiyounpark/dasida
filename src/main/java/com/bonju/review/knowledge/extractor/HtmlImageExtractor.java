package com.bonju.review.knowledge.extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HtmlImageExtractor implements ImageExtractor {

    public List<String> extractImageSrc(String html) {
        Document document = Jsoup.parse(html);
        Elements imgElements = document.getElementsByTag("img");

        List<String> srcList = new ArrayList<>();
        for (Element img : imgElements) {
            String src = img.attr("src");
            srcList.add(src);
        }

        return srcList;
    }
}