package com.bonju.review.wrong_answer_note.service;

import com.bonju.review.quiz.entity.UserAnswer;

public interface WrongAnswerNoteWriteService {
    void addWrongAnswer(UserAnswer userAnswer);
}
