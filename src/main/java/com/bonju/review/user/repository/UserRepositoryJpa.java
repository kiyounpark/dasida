package com.bonju.review.user.repository;

import com.bonju.review.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryJpa implements UserRepository {

    private final EntityManager em;

    public Optional<User> findByKaKaoId(String kakaoId){
        return em.createQuery("SELECT u FROM User u WHERE u.kakaoId = :kakaoId", User.class)
                .setParameter("kakaoId", kakaoId)
                .getResultStream() // 결과를 스트림으로 변환
                .findFirst(); // 첫 번째 요소를 Optional로 반환
    }

    public User save(User user) {
        if (user.getId() == null) {
            // If the user is new (id is null), persist it
            em.persist(user);
            return user;
        } else {
            // If the user already exists, merge (update) it
            return em.merge(user);
        }
    }

    @Override
    public long count() {
        // JPQL 로 전체 사용자 수 반환
        return em.createQuery("select count(u) from User u", Long.class)
                .getSingleResult();
    }
}
