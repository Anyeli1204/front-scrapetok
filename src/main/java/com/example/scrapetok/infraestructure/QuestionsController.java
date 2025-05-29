package com.example.scrapetok.infraestructure;

import com.example.scrapetok.application.QuestionsAndAnswersService;
import com.example.scrapetok.domain.DTO.UserQuestionRequestDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
public class QuestionsController {
    @Autowired
    private QuestionsAndAnswersService questionsAndAnswersService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/createQuestion")
    public ResponseEntity<?> makeQuestion(@RequestBody @Valid UserQuestionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionsAndAnswersService.assignQuestion(request));
    };

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/getAllQuestions")
    public ResponseEntity<?> getAllQuestion() {
        return ResponseEntity.status(HttpStatus.OK).body(questionsAndAnswersService.getQuestions());
    };
}
