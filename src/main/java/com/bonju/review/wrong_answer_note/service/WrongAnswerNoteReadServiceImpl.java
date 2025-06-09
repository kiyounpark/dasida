package com.bonju.review.wrong_answer_note.service;

import com.bonju.review.quiz.entity.Quiz;
import com.bonju.review.useranswer.entity.UserAnswer;
import com.bonju.review.user.entity.User;
import com.bonju.review.user.service.UserService;
import com.bonju.review.wrong_answer_note.dto.WrongAnswerResponseDto;
import com.bonju.review.wrong_answer_note.dto.WrongAnswerGroupResponseDto;
import com.bonju.review.wrong_answer_note.entity.WrongAnswerNote;
import com.bonju.review.wrong_answer_note.repository.WrongAnswerNoteReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WrongAnswerNoteReadServiceImpl implements WrongAnswerNoteReadService {

    private final UserService userService;
    private final WrongAnswerNoteReadRepository readRepository;

    @Override
    public List<WrongAnswerGroupResponseDto> getUserWrongAnswersGroupedByQuiz() {
        User user = userService.findUser();
        // 1) DB 조회 (JOIN FETCH 등으로 WrongAnswerNote + UserAnswer + Quiz 로딩)
        List<WrongAnswerNote> notes = readRepository.findByUserAnswerId(user.getId());

        // 2) quizId 기준으로 그룹화
        Map<Long, List<WrongAnswerNote>> mapByQuizId = notes.stream()
                .collect(Collectors.groupingBy(
                        note -> note.getUserAnswer().getQuiz().getId()
                ));

        // 3) 변환
        List<WrongAnswerGroupResponseDto> result = new ArrayList<>();
        for (Map.Entry<Long, List<WrongAnswerNote>> entry : mapByQuizId.entrySet()) {
            Long quizId = entry.getKey();
            List<WrongAnswerNote> groupedNotes = entry.getValue();

            // quiz (동일한 quizId 그룹이므로 첫번째 항목에서 getQuiz()해도 모두 동일)
            Quiz quiz = groupedNotes.getFirst().getUserAnswer().getQuiz();

            // answers (사용자 답변 목록)
            List<WrongAnswerResponseDto> answerDtos = groupedNotes.stream()
                    .map(this::convertNoteToDto)
                    .toList();

            // 🎯 DTO 생성 시 퀴즈 정답(quiz.getAnswer())도 포함
            WrongAnswerGroupResponseDto groupDto = new WrongAnswerGroupResponseDto(
                    quizId,
                    quiz.getQuestion(),         // 퀴즈 문제
                    quiz.getAnswer(),       // 퀴즈 정답 추가!
                    answerDtos
            );
            result.add(groupDto);
        }
        return result;
    }

    private WrongAnswerResponseDto convertNoteToDto(WrongAnswerNote note) {
        UserAnswer ua = note.getUserAnswer();
        return new WrongAnswerResponseDto(
                ua.getId(),
                ua.getAnswer(), // 사용자 입력 답
                ua.getDayType()
        );
    }
}
