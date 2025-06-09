package com.bonju.review.wrong_answer_note.service;

import com.bonju.review.useranswer.entity.UserAnswer;
import com.bonju.review.wrong_answer_note.entity.WrongAnswerNote;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WrongAnswerNoteWriteServiceImpl implements WrongAnswerNoteWriteService {

    private final EntityManager em;

    @Override
    public void addWrongAnswer(UserAnswer userAnswer) {
        WrongAnswerNote wrongAnswerNote = new WrongAnswerNote(userAnswer);
        em.persist(wrongAnswerNote);
    }
}
