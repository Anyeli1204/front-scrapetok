package com.example.scrapetok.config;

import com.example.scrapetok.application.TopGlobalEmailService;
import com.example.scrapetok.domain.DTO.TopGlobalEmailDTO;
import com.example.scrapetok.infraestructure.TopGlobalEmailController;
import com.example.scrapetok.security.JwtUtil;
import com.example.scrapetok.security.MyUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(TopGlobalEmailController.class)
public class TopGlobalEmailControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;


    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    @MockitoBean
    private TopGlobalEmailService topGlobalEmailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendEmail_WithValidList_ReturnsOk() throws Exception {
        TopGlobalEmailDTO dto = new TopGlobalEmailDTO();
        dto.setAdminId(1L);
        dto.setUsedHashTag("trending");
        dto.setDatePosted(LocalDate.of(2024, 5, 25));
        dto.setUsernameTiktokAccount("user123");
        dto.setPostURL("https://tiktok.com/@user123/video/123");
        dto.setViews(100);
        dto.setLikes(10);
        dto.setEngagement(5.5);

        List<TopGlobalEmailDTO> requestList = List.of(dto);

        Mockito.doNothing().when(topGlobalEmailService).sendTopGlobalTextEmail(Mockito.anyList());

        mockMvc.perform(post("/admin/sendemail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestList)))
                .andExpect(status().isOk())
                .andExpect(content().string("✅ Top daily global emails have been sent successfully.\""));

        Mockito.verify(topGlobalEmailService).sendTopGlobalTextEmail(Mockito.anyList());
    }

    @Test
    void sendEmail_WithEmptyList_ReturnsOk() throws Exception {
        List<TopGlobalEmailDTO> emptyList = List.of();

        Mockito.doNothing().when(topGlobalEmailService).sendTopGlobalTextEmail(Mockito.anyList());

        mockMvc.perform(post("/admin/sendemail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyList)))
                .andExpect(status().isOk())
                .andExpect(content().string("✅ Top daily global emails have been sent successfully.\""));

        Mockito.verify(topGlobalEmailService).sendTopGlobalTextEmail(Mockito.anyList());
    }
}