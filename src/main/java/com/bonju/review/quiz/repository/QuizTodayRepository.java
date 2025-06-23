package com.bonju.review.quiz.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * ▸ 3‧7‧30일차 복습 대상이면서, 오늘 아직 아무도 풀지 않은 퀴즈 중
 *   사용자별로 가장 오래된(createdAt 최소) 퀴즈 ID를 하나씩 추려 반환한다.
 *
 *  - JPQL 서브쿼리(min)로 윈도 함수 대체
 *  - 날짜 파라미터는 LocalDate 고정 값으로 주입
 */
@Repository
@RequiredArgsConstructor
public class QuizTodayRepository {

  @PersistenceContext
  private final EntityManager em;

  private static final String TODAY_QUIZ_JPQL = """
    select q.id
    from   Quiz q
    where  cast(q.createdAt as date) in (:d3, :d7, :d30)
      and  not exists (
             select 1
             from   UserAnswer ua
             where  ua.quiz = q
               and  cast(ua.createdAt as date) = :today
           )
      and  q.createdAt = (
             select max(q2.createdAt)
             from   Quiz q2
             where  q2.user = q.user
               and  cast(q2.createdAt as date) in (:d3, :d7, :d30)
               and  not exists (
                      select 1
                      from   UserAnswer ua2
                      where  ua2.quiz = q2
                        and  cast(ua2.createdAt as date) = :today
                    )
           )
    """;

  public List<Long> findTodayQuizIds() {
    LocalDate today = LocalDate.now();
    return em.createQuery(TODAY_QUIZ_JPQL, Long.class)
            .setParameter("d3",  today.minusDays(3))
            .setParameter("d7",  today.minusDays(7))
            .setParameter("d30", today.minusDays(30))
            .setParameter("today", today)
            .getResultList();
  }
}