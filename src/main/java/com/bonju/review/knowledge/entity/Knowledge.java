package com.bonju.review.knowledge.entity;

import com.bonju.review.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Knowledge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;       // 예: "영어 단어", "수학 공식" 등

    @Lob
    private String content; // 예: "토익 대비 단어 모음"

    private LocalDateTime createdAt;

    @Builder
    private Knowledge(User user ,String title, String content, LocalDateTime createdAt) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }
}
