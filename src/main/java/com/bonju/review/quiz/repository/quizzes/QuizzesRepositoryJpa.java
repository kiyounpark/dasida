package com.bonju.review.quiz.repository.quizzes;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizzesRepositoryJpa implements QuizzesRepository {

    private final EntityManager em;

    /* ──────────────────────────────────────────────
     * public API
     * ──────────────────────────────────────────── */

    /**
     * days 전 구간에서
     *   ① UserAnswer 가 전혀 없거나
     *   ② UserAnswer 는 있지만 한 번도 맞힌 적이 없는
     * Quiz 만 createdAt DESC 로 반환.
     */
    @Override
    public List<Quiz> findUnsolvedOrAlwaysWrongQuizzes(User user, int days) {
        Range r = range(days);

        return em.createQuery("""
                SELECT q
                FROM   Quiz q
                WHERE  q.user        = :user
                  AND  q.createdAt  >= :start
                  AND  q.createdAt  <  :end
                  AND  NOT EXISTS (
                         SELECT 1
                         FROM   UserAnswer ua
                         WHERE  ua.quiz      = q
                           AND  ua.user      = :user
                           AND  ua.isCorrect = true
                           AND  ua.createdAt >= :start   
                           AND  ua.createdAt <  :end
                       )
                ORDER BY q.createdAt DESC,  
                         q.id        DESC
                """, Quiz.class)
                .setParameter("user",  user)
                .setParameter("start", r.start)
                .setParameter("end",   r.end)
                .getResultList();
    }

    /* ──────────────────────────────────────────────
     * private helpers
     * ──────────────────────────────────────────── */

    /** days 전 00:00 ~ (days-1) 전 00:00 미만 범위 계산 */
    private static Range range(int days) {
        LocalDateTime start = LocalDate.now()
                .minusDays(days)
                .atStartOfDay();
        LocalDateTime end   = start.plusDays(1);   // 다음 날 00:00
        return new Range(start, end);
    }

    private record Range(LocalDateTime start, LocalDateTime end) {}
}