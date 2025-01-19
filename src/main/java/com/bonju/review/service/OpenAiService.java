package com.bonju.review.service;

import com.bonju.review.client.OpenAiClient;
import com.bonju.review.dto.QuizDto;
import com.bonju.review.dto.QuizWrapperDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final OpenAiClient openAiClient;


}
