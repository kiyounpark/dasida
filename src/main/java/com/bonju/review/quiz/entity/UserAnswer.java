package com.bonju.review.quiz.entity;

import com.bonju.review.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
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

    private boolean isWrong;

    private LocalDateTime answeredAt = LocalDateTime.now();

    public UserAnswer(User user, Quiz quiz, String answer, int dayType, boolean isWrong) {
        this.user = user;
        this.quiz = quiz;
        this.answer = answer;
        this.dayType = dayType;
        this.isWrong = isWrong;
    }
}