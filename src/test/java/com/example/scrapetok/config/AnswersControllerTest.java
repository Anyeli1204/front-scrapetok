package com.example.scrapetok.config;

import com.example.scrapetok.application.QuestionsAndAnswersService;
import com.example.scrapetok.domain.DTO.AdminAnswerRequestDTO;
import com.example.scrapetok.domain.DTO.FullAnswerQuestionResponseDTO;
import com.example.scrapetok.infraestructure.AnswersController;
import com.example.scrapetok.security.JwtUtil;
import com.example.scrapetok.security.MyUserDetailsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AnswersController.class)
public class AnswersControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    @MockitoBean
    private QuestionsAndAnswersService questionsAndAnswersService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @InjectMocks
    private AnswersController answersController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void answerQuestion_ReturnsOkResponse() throws Exception {
        AdminAnswerRequestDTO request = new AdminAnswerRequestDTO();
        request.setAdminId(1L);
        request.setQuestionId(2L);
        request.setAnswerDescription("Respuesta de prueba");

        FullAnswerQuestionResponseDTO responseDto = new FullAnswerQuestionResponseDTO();
        // configura responseDto con valores si quieres validar contenido específico

        when(questionsAndAnswersService.replyQuestion(any())).thenReturn(responseDto);

        mockMvc.perform(patch("/admin/answerQuestion")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        verify(questionsAndAnswersService, times(1)).replyQuestion(any());
    }
}