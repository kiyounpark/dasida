package com.bonju.review.knowledge.entity;

import com.bonju.review.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String text; // 예: "토익 대비 단어 모음"

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "knowledge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KnowledgeImage> knowledgeImages = new ArrayList<>();

    @Builder
    private Knowledge(User user, String title, String text, LocalDateTime createdAt, List<KnowledgeImage> knowledgeImages) {
        this.user = user;
        this.title = title;
        this.text = text;
        this.createdAt = createdAt;
        addKnowledgeImages(knowledgeImages);
    }

    private void addKnowledgeImages(List<KnowledgeImage> knowledgeImages) {
        if (knowledgeImages == null) {
            return;
        }

        knowledgeImages.forEach(this::addKnowledgeImage);
    }

    public void addKnowledgeImage(KnowledgeImage knowledgeImage) {
        if (knowledgeImage == null) {
            return;
        }

        if (this.knowledgeImages.contains(knowledgeImage)) {
            return;
        }

        this.knowledgeImages.add(knowledgeImage);
        knowledgeImage.assignKnowledge(this);
    }
}
