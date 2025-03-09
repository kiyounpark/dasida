package com.bonju.review.wrong_answer_note.service;

import com.bonju.review.wrong_answer_note.dto.WrongAnswerGroupResponseDto;

import java.util.List;

public interface WrongAnswerNoteReadService {
    // 특정 사용자의 오답노트 목록 조회
    public List<WrongAnswerGroupResponseDto> getUserWrongAnswersGroupedByQuiz();
}
