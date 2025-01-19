package com.bonju.review.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Knowledge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;       // 예: "영어 단어", "수학 공식" 등
    private String description; // 예: "토익 대비 단어 모음"

    public Knowledge(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
