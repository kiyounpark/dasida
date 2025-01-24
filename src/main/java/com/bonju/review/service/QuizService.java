package com.bonju.review.service;

import com.bonju.review.dto.QuizResponseDto;
import com.bonju.review.entity.Quiz;
import com.bonju.review.entity.User;
import com.bonju.review.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final UserService userService;

    /**
     * Knowledge ID가 같은 Quiz들끼리 묶어서
     * List<List<QuizResponseDto>> 형태로 반환
     */
    public List<List<QuizResponseDto>> getGroupedQuizzesByKnowledge() {

        User user = userService.findUserByKaKaoId();
        // 1) 모든 Quiz(또는 필요 조건) 조회
        List<Quiz> quizList = quizRepository.findQuizzesCreatedWithin30DaysByUser(user.getId());

        // 2) knowledge_id 기준으로 그룹화
        //    key: knowledge_id, value: Quiz 목록
        Map<Long, List<Quiz>> groupedByKnowledge = quizList.stream()
                .collect(Collectors.groupingBy(q -> q.getKnowledge().getId()));

        // 3) 각 그룹(List<Quiz>)을 List<QuizResponseDto>로 변환
        List<List<QuizResponseDto>> result = new ArrayList<>();

        for (Map.Entry<Long, List<Quiz>> entry : groupedByKnowledge.entrySet()) {
            List<Quiz> sameKnowledgeQuizzes = entry.getValue();

            // Quiz -> QuizResponseDto 매핑
            List<QuizResponseDto> dtoList = sameKnowledgeQuizzes.stream()
                    .map(quiz -> new QuizResponseDto(
                            quiz.getId(),
                            quiz.getQuiz()
                    ))
                    .collect(Collectors.toList());

            result.add(dtoList);
        }

        return result;
    }
}
