package com.bonju.review.devicetoken.entity;

import com.bonju.review.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, length = 255)
  private String token;

  @Column(nullable = false)
  private boolean active = true;

  @Builder
  private DeviceToken(User user, String token) {
    this.user = user;
    this.token = token;
  }

  public void deactivate() {
    this.active = false;
  }

  public void refresh(String newToken) {
    this.token = newToken;
    this.active = true;
  }
}