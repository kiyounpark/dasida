package com.bonju.review.wrong_answer_note.repository;

import com.bonju.review.wrong_answer_note.entity.WrongAnswerNote;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WrongAnswerNoteWriteRepositoryJpa implements WrongAnswerNoteWriteRepository {
    private final EntityManager em;

    @Override
    public void save(WrongAnswerNote note) {
        em.persist(note);
    }
}
