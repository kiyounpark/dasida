package com.bonju.review.controller;

import com.bonju.review.dto.DayQuizResponseDto;
import com.bonju.review.dto.HomeResponseDto;
import com.bonju.review.service.QuizzesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final QuizzesService quizzesService;

    @GetMapping
    public HomeResponseDto getQuizzesAndKnowledge() {
        List<DayQuizResponseDto> dayQuizzes = quizzesService.getAllDayQuizzes();
        return new HomeResponseDto(dayQuizzes);
    }
}
