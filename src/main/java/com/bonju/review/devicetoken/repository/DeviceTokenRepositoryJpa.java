package com.bonju.review.devicetoken.repository;

import com.bonju.review.devicetoken.entity.DeviceToken;
import com.bonju.review.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DeviceTokenRepositoryJpa implements DeviceTokenRepository {

  private final EntityManager em;

  /**
   * 해당 사용자의 디바이스 토큰 하나만 Optional 로 반환한다.
   */
  @Override
  public Optional<DeviceToken> findByUser(User user) {
    return em.createQuery(
                    "select d " +
                            "from DeviceToken d " +
                            "where d.user = :user " +
                            "order by d.id desc",
                    DeviceToken.class)
            .setParameter("user", user)
            .setMaxResults(1)
            .getResultStream()
            .findFirst();
  }

  public Optional<DeviceToken> findByUserIdAndToken(User user, String token) {
    return em.createQuery(
                    "select d " +
                            "from DeviceToken d " +
                            "where d.user = :user          " +   // ← 엔티티 자체 비교
                            "  and d.token = :token",            //    (Hibernate 는 PK 값으로 비교)
                    DeviceToken.class)
            .setParameter("user",  user)        // detached 여도 PK(id)만 있으면 OK
            .setParameter("token", token)
            .getResultStream()
            .findFirst();
  }


  @Override
  public DeviceToken save(DeviceToken token) {
    em.persist(token);
    return token;
  }
}
