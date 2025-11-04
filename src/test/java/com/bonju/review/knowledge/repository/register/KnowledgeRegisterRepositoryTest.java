package com.bonju.review.knowledge.repository.register;

import com.bonju.review.knowledge.entity.Knowledge;
import com.bonju.review.knowledge.entity.KnowledgeImage;
import com.bonju.review.user.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(KnowledgeRegisterRepositoryImpl.class)
class KnowledgeRegisterRepositoryTest {

    @Autowired
    private KnowledgeRegisterRepository knowledgeRegisterRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("지식을 등록하면 연결된 이미지들이 함께 저장되고 지식과 양방향 연관 관계를 유지한다.")
    void registerKnowledgeWithImages() {
        LocalDateTime createdAt = LocalDateTime.of(2024, 12, 25, 9, 30);
        User writer = User.builder()
                .kakaoId("kakao-user-100")
                .nickname("knowledgeWriter")
                .build();
        entityManager.persist(writer);

        List<KnowledgeImage> knowledgeImages = List.of(
                KnowledgeImage.builder().imageUrl("https://image.server/knowledge-1.png").build(),
                KnowledgeImage.builder().imageUrl("https://image.server/knowledge-2.png").build()
        );

        Knowledge knowledge = Knowledge.builder()
                .user(writer)
                .title("자바 컬렉션 요약")
                .text("List와 Set의 차이점을 정리한다.")
                .createdAt(createdAt)
                .knowledgeImages(knowledgeImages)
                .build();

        Knowledge savedKnowledge = knowledgeRegisterRepository.registerKnowledge(knowledge);
        entityManager.flush();
        entityManager.clear();

        Knowledge foundKnowledge = entityManager.find(Knowledge.class, savedKnowledge.getId());

        assertThat(foundKnowledge.getKnowledgeImages()).hasSize(2);
        assertThat(foundKnowledge.getKnowledgeImages())
                .allMatch(image -> image.getKnowledge().getId().equals(foundKnowledge.getId()));
        assertThat(foundKnowledge.getKnowledgeImages())
                .extracting(KnowledgeImage::getImageUrl)
                .containsExactlyInAnyOrder(
                        "https://image.server/knowledge-1.png",
                        "https://image.server/knowledge-2.png"
                );
    }
}
