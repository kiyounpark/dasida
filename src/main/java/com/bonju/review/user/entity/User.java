package com.bonju.review.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Required by JPA
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String kakaoId;

    private String nickname;

    // Constructor for immutability
    @Builder
    private User(String kakaoId, String nickname) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
    }

    public void updateUserNickname(String nickname){
        this.nickname = nickname;
    }

}
