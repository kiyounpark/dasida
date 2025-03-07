package com.bonju.review.quiz.repository.quizzes;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class QuizzesRepositoryJpa implements QuizzesRepository{

    private final EntityManager em;

    /**
     * 지정한 days 값에 해당하는 날짜 범위(예: days=3이면, 3일 전 0시 ~ 2일 전 0시 미만)에 해당하는 퀴즈를 조회합니다.
     */
    public List<Quiz> findQuizzesByDaysAgo(User user, int days) {
        LocalDateTime start = LocalDate.now().minusDays(days).atStartOfDay();
        LocalDateTime end = LocalDate.now().minusDays(days - 1).atStartOfDay();

        return em.createQuery(
                        "SELECT q FROM Quiz q WHERE q.user = :user AND q.createdAt >= :start AND q.createdAt < :end",
                        Quiz.class)
                .setParameter("user", user)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}
