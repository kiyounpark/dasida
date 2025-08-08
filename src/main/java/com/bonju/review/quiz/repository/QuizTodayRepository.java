package com.bonju.review.quiz.repository;

import com.bonju.review.quiz.entity.Quiz;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * ▸ 3‧7‧30일차 복습 대상이면서, 오늘 아직 아무도 풀지 않은 퀴즈 중
 *   사용자별로 가장 얼마되지 않은(createdAt 최소) 퀴즈 ID를 하나씩 추려 반환한다.
 *  - JPQL 서브쿼리(min)로 윈도 함수 대체
 *  - 날짜 파라미터는 LocalDate 고정 값으로 주입
 */
@Repository
@RequiredArgsConstructor
public class QuizTodayRepository {

  private final EntityManager em;

  /** 0‧3‧7‧30 일차 + 미풀이 퀴즈 중
   *   사용자별로 가장 최근(createdAt max) 퀴즈 1건을 고르되,
   *   같은 시각이면 id 가 더 큰 행을 우선 */
  private static final String TODAY_QUIZ_JPQL = """
    select q.id
    from   Quiz q
    where  cast(q.createdAt as date) in (:d0, :d3, :d7, :d30)
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
               and  cast(q2.createdAt as date) in (:d0, :d3, :d7, :d30)
               and  not exists (
                      select 1
                      from   UserAnswer ua2
                      where  ua2.quiz = q2
                        and  cast(ua2.createdAt as date) = :today
                    )
           )
      and  q.id = (
             select max(q3.id)
             from   Quiz q3
             where  q3.user       = q.user
               and  q3.createdAt  = q.createdAt   
           )
    """;

  public List<Long> findTodayQuizIds() {
    LocalDate today = LocalDate.now();
    return em.createQuery(TODAY_QUIZ_JPQL, Long.class)
            .setParameter("d0",  today)
            .setParameter("d3",  today.minusDays(3))
            .setParameter("d7",  today.minusDays(7))
            .setParameter("d30", today.minusDays(30))
            .setParameter("today", today)
            .getResultList();
  }

  public List<Quiz> findByIdsWithUser(List<Long> ids) {
    if (ids == null || ids.isEmpty()) return Collections.emptyList();

    return em.createQuery(
                    "select q from Quiz q " +
                            "join fetch q.user " +
                            "where q.id in :ids", Quiz.class)
            .setParameter("ids", ids)
            .getResultList();
  }
}