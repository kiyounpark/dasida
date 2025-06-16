package com.bonju.review.wrong_answer_note.controller;

import com.bonju.review.wrong_answer_note.WrongNoteService;
import com.bonju.review.wrong_answer_note.dto.WrongAnswerGroupResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class WrongNoteController {

  private final WrongNoteService wrongNoteService;

  @GetMapping("/wrong-notes")
  public ResponseEntity<List<WrongAnswerGroupResponseDto>> getAllWrongNotes() {
    return ResponseEntity.ok(wrongNoteService.loadWrongNote());
  }
}
