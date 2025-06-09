package com.bonju.review.wrong_answer_note.entity;

import com.bonju.review.useranswer.entity.UserAnswer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WrongAnswerNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 오답 노트에 기록된 시각
    private final LocalDateTime registeredAt = LocalDateTime.now();

    // 어떤 사용자 답변(UserAnswer)을 참조하는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_answer_id")
    private UserAnswer userAnswer;

    public WrongAnswerNote(UserAnswer userAnswer) {
        this.userAnswer = userAnswer;
    }
}