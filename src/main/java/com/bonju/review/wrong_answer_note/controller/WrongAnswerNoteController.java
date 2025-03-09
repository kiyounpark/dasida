package com.bonju.review.wrong_answer_note.controller;

import com.bonju.review.wrong_answer_note.dto.WrongAnswerGroupResponseDto;
import com.bonju.review.wrong_answer_note.service.WrongAnswerNoteReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wrong-answer-notes")
@RequiredArgsConstructor
public class WrongAnswerNoteController {

    private final WrongAnswerNoteReadService wrongAnswerNoteReadService;

    /**
     * 사용자별 오답노트 조회 - 퀴즈별로 묶어서 반환
     */
    @GetMapping
    public ResponseEntity<List<WrongAnswerGroupResponseDto>> getWrongAnswersGroupedByQuiz(
    ) {
        // 1) 서비스 호출 → 사용자(userId)의 오답노트 목록을 퀴즈별로 묶어 DTO 변환
        List<WrongAnswerGroupResponseDto> result = wrongAnswerNoteReadService.getUserWrongAnswersGroupedByQuiz();

        // 2) JSON 형태로 응답
        return ResponseEntity.ok(result);
    }
}
