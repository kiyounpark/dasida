package com.bonju.review.home.controller;

import com.bonju.review.home.workflow.HomeWorkflow;
import com.bonju.review.knowledge.dto.DayKnowledgeResponseDto;
import com.bonju.review.knowledge.service.KnowledgeReadService;
import com.bonju.review.quiz.dto.DayQuizResponseDto;
import com.bonju.review.home.dto.HomeResponseDto;
import com.bonju.review.knowledge.service.knowledge_list.TodayKnowledgeListService;
import com.bonju.review.quiz.service.quizzes.QuizzesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeWorkflow homeWorkflow;

    @GetMapping
    public HomeResponseDto getQuizzesAndKnowledge() {
        return homeWorkflow.buildHomeResponse();
    }
}
