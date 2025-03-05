package com.bonju.review.entity;

import com.bonju.review.enums.AnswerStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Enumerated(EnumType.STRING)
    @Setter
    private AnswerStatus answerStatus1d = AnswerStatus.NOT_ATTEMPTED;

    @Enumerated(EnumType.STRING)
    @Setter
    private AnswerStatus answerStatus3d = AnswerStatus.NOT_ATTEMPTED;

    @Enumerated(EnumType.STRING)
    @Setter
    private AnswerStatus answerStatus7d = AnswerStatus.NOT_ATTEMPTED;

    @Enumerated(EnumType.STRING)
    @Setter
    private AnswerStatus answerStatus30d = AnswerStatus.NOT_ATTEMPTED;

    private final LocalDateTime createdAt = LocalDateTime.now();

    public Quiz(User user ,Knowledge knowledge, String quiz, String answer, String hint) {
        this.user = user;
        this.knowledge = knowledge;
        this.quiz = quiz;
        this.answer = answer;
        this.hint = hint;
    }
}
