package com.bonju.review.quiz.entity;

import com.bonju.review.user.entity.User;
import com.bonju.review.knowledge.entity.Knowledge;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
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

    private String question;
    private String answer;
    private String hint;
    private final LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    public Quiz(User user ,Knowledge knowledge, String question, String answer, String hint) {
        this.user = user;
        this.knowledge = knowledge;
        this.question = question;
        this.answer = answer;
        this.hint = hint;
    }

    // 이제 막 생각해보자 뭘 해야할지 그냥 여기에 적어
    // 지식을 꼭 알고 있어야할까?
    // 왜냐면 지식에 의존적이게 되니까 한번 생각을 해보자.
    // 퀴즈를 불러올때 지식의 장점은 틀렸을때 지식에 가서 공부할수 있다는거지
    // 단점은 퀴즈 서비스에서 내에서 생성하는게 아닌 외부에서 받아야한다는거지
    // 그게 정석이라네.
    // 여기서 문제가 또 나오네. 아 잠깐만 문제가 정말 나올까?
    // controller에서 이런 과정들이 이뤄질거같아.
    // service에서 지식 엔티티를 던지는거야.
    // 그리고 location에 getId를 통해 지정을 하고, 퀴즈 서비스에서는 엔티티를 전달하는게 이게 정말 괜찮은걸까?
    // 컨트롤러 단에서 엔티티를 다루는게 좀 무서운데
    public int getAnswerLength(){
        return answer.length();
    }

    public boolean isCorrectAnswer(String answerText) {
        return answer.equals(answerText);
    }
}
