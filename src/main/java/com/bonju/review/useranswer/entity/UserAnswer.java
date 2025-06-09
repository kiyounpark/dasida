package com.bonju.review.useranswer.entity;

import com.bonju.review.quiz.entity.Quiz;
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
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    private String answer;

    private int dayType;

    private boolean isCorrect;

    private final LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    public UserAnswer(User user, Quiz quiz, String answer, int dayType, boolean isCorrect) {
        this.user = user;
        this.quiz = quiz;
        this.answer = answer;
        this.dayType = dayType;
        this.isCorrect = isCorrect;
    }
}