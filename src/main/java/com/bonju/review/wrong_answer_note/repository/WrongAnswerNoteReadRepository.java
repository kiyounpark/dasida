package com.bonju.review.wrong_answer_note.repository;

import com.bonju.review.wrong_answer_note.entity.WrongAnswerNote;

import java.util.List;

public interface WrongAnswerNoteReadRepository {
    List<WrongAnswerNote> findByUserAnswerId(Long userAnswerId);
}
