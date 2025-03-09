package com.bonju.review.wrong_answer_note.repository;

import com.bonju.review.wrong_answer_note.entity.WrongAnswerNote;

public interface WrongAnswerNoteWriteRepository {
    void save(WrongAnswerNote note);
}