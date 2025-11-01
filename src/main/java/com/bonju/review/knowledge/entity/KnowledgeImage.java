package com.bonju.review.knowledge.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KnowledgeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "knowledge_id")
    private Knowledge knowledge;

    @Column(nullable = false)
    private String imageUrl;

    @Builder
    private KnowledgeImage(Knowledge knowledge, String imageUrl) {
        this.knowledge = knowledge;
        this.imageUrl = imageUrl;
    }

    void assignKnowledge(Knowledge knowledge) {
        this.knowledge = knowledge;
    }
}
