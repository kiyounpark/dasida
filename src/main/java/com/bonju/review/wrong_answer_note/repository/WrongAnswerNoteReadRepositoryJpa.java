package com.bonju.review.wrong_answer_note.repository;

import com.bonju.review.wrong_answer_note.entity.WrongAnswerNote;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WrongAnswerNoteReadRepositoryJpa implements WrongAnswerNoteReadRepository {

    private final EntityManager em;

    @Override
    public List<WrongAnswerNote> findByUserAnswerId(Long userAnswerId) {
        return em.createQuery(
                        "SELECT wan FROM WrongAnswerNote wan " +
                                "JOIN FETCH wan.userAnswer ua " +    // WrongAnswerNote → UserAnswer 패치 조인
                                "JOIN FETCH ua.quiz q " +           // UserAnswer → Quiz 패치 조인
                                "WHERE ua.id = :userAnswerId",      // 특정 UserAnswer만 필터링 (예시)
                        WrongAnswerNote.class
                )
                .setParameter("userAnswerId", userAnswerId)
                .getResultList();
        }
    }