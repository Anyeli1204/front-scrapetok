package com.example.scrapetok.application;

import com.example.scrapetok.domain.AdminProfile;
import com.example.scrapetok.domain.DTO.AdminAnswerRequestDTO;
import com.example.scrapetok.domain.DTO.FullAnswerQuestionResponseDTO;
import com.example.scrapetok.domain.DTO.UserQuestionRequestDTO;
import com.example.scrapetok.domain.GeneralAccount;
import com.example.scrapetok.domain.QuestAndAnswer;
import com.example.scrapetok.domain.enums.statusQA;
import com.example.scrapetok.exception.ResourceNotFoundException;
import com.example.scrapetok.repository.AdminProfileRepository;
import com.example.scrapetok.repository.GeneralAccountRepository;
import com.example.scrapetok.repository.QuestionAndAnswerRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class QuestionsAndAnswersService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private QuestionAndAnswerRepository questionAndAnswerRepository;
    @Autowired
    private AdminProfileRepository adminProfileRepository;
    @Autowired
    private GeneralAccountRepository generalAccountRepository;

    // Usuario hace una pregunta
    public FullAnswerQuestionResponseDTO assignQuestion(UserQuestionRequestDTO request) {
        GeneralAccount user = generalAccountRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("No user with ID " + request.getUserId()));

        QuestAndAnswer questAndAnswer = new QuestAndAnswer();
        questAndAnswer.setUser(user);
        questAndAnswer.setQuestionDescription(request.getQuestionDescription());

        ZonedDateTime zonedDateTime = obtenerFechaYHoraDePeru();
        questAndAnswer.setQuestionDate(zonedDateTime.toLocalDate());
        questAndAnswer.setQuestionHour(zonedDateTime.toLocalTime().withNano(0));
        questAndAnswer.setStatus(statusQA.PENDING);

        QuestAndAnswer saved = questionAndAnswerRepository.save(questAndAnswer);
        return modelMapper.map(saved, FullAnswerQuestionResponseDTO.class);
    }


    // Usuario visualiza todas las preguntas pendientes y respondidas
    public List<FullAnswerQuestionResponseDTO> getQuestions() {
        List<QuestAndAnswer> questions = questionAndAnswerRepository.findAll();
        if (questions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No questions were registered.");
        }
        List<FullAnswerQuestionResponseDTO> ResponseDTOs = new ArrayList<>();
        for (QuestAndAnswer questAndAnswer : questions) {
            FullAnswerQuestionResponseDTO responseDTO = modelMapper.map(questAndAnswer,FullAnswerQuestionResponseDTO.class);
            ResponseDTOs.add(responseDTO);
        }
        return ResponseDTOs;
    }


    // Admin responde una pregunta
    public FullAnswerQuestionResponseDTO replyQuestion(AdminAnswerRequestDTO request) throws ResourceNotFoundException, ResponseStatusException {
        AdminProfile admin = adminProfileRepository.findById(request.getAdminId()).orElseThrow(() -> new ResourceNotFoundException("No admin with ID " + request.getAdminId()));
        QuestAndAnswer questAndAnswer = questionAndAnswerRepository.findById(request.getQuestionId()).orElseThrow(()-> new ResourceNotFoundException("No question with ID " + request.getQuestionId()));
        if (questAndAnswer.getStatus() == statusQA.ANSWERED) {
            throw new IllegalStateException("This question has already been answered.");
        }
        questAndAnswer.setAnswerDescription(request.getAnswerDescription());
        questAndAnswer.setAdmin(admin);
        questAndAnswer.setStatus(statusQA.ANSWERED);

        ZonedDateTime zonedDateTime = obtenerFechaYHoraDePeru();
        questAndAnswer.setAnswerDate(zonedDateTime.toLocalDate());
        questAndAnswer.setAnswerHour(zonedDateTime.toLocalTime().withNano(0));
        QuestAndAnswer saved = questionAndAnswerRepository.save(questAndAnswer);
        Integer answerMade = admin.getTotalQuestionsAnswered();
        admin.setTotalQuestionsAnswered(answerMade + 1);
        adminProfileRepository.save(admin);
        return modelMapper.map(saved, FullAnswerQuestionResponseDTO.class);
    }


    private ZonedDateTime obtenerFechaYHoraDePeru() {
        return ZonedDateTime.now(ZoneId.of("America/Lima"));
    }
}
