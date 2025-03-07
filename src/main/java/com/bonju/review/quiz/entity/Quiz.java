package com.bonju.review.quiz.entity;

import com.bonju.review.user.entity.User;
import com.bonju.review.knowledge.entity.Knowledge;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "knowledge_id")
    private Knowledge knowledge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String quiz;
    private String answer;
    private String hint;
    private final LocalDateTime createdAt = LocalDateTime.now();

    public Quiz(User user ,Knowledge knowledge, String quiz, String answer, String hint) {
        this.user = user;
        this.knowledge = knowledge;
        this.quiz = quiz;
        this.answer = answer;
        this.hint = hint;
    }
}
