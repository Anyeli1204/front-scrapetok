package com.example.scrapetok.config;

import com.example.scrapetok.application.DBQueryService;
import com.example.scrapetok.domain.DTO.UserDBQueryRequest;
import com.example.scrapetok.domain.DTO.UserTiktokMetricsResponseDTO;
import com.example.scrapetok.infraestructure.DataBaseQueriesController;
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
import java.time.LocalTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(DataBaseQueriesController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DataBaseQueriesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DBQueryService dbQueryService;

    @MockitoBean
    private MyUserDetailsService userDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void makeDbQueries_ReturnsOkWithListResponse() throws Exception {
        // Request simulado
        UserDBQueryRequest request = new UserDBQueryRequest();
        request.setUserId(1L);

        // Respuesta esperada
        UserTiktokMetricsResponseDTO dto = new UserTiktokMetricsResponseDTO();
        dto.setId(1L);
        dto.setUserId(1L);
        dto.setPostId("abc123");
        dto.setDatePosted(LocalDate.of(2024, 5, 10));
        dto.setHourPosted(LocalTime.of(14, 30));
        dto.setUsernameTiktokAccount("testuser");
        dto.setViews(1000);
        dto.setLikes(100);
        dto.setComments(10);
        dto.setSaves(5);
        dto.setReposts(2);
        dto.setTotalInteractions(117);
        dto.setEngagement(11.7);
        dto.setNumberHashtags(3);
        dto.setHashtags("#test");
        dto.setSoundId("sound123");
        dto.setSoundURL("http://sound.com/123");
        dto.setRegionPost("US");
        dto.setDateTracking(LocalDate.of(2024, 5, 12));
        dto.setTimeTracking(LocalTime.of(15, 0));

        // Simular comportamiento del servicio
        //Mockito.when(dbQueryService.buscarConFiltros(Mockito.any())).thenReturn(Collections.singletonList(dto));

        // Ejecutar request y verificar
        mockMvc.perform(post("/user/dbquery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].postId").value("abc123"))
                .andExpect(jsonPath("$[0].views").value(1000));
    }

    @Test
    void makeDbQueries_ReturnsEmptyList() throws Exception {
        UserDBQueryRequest request = new UserDBQueryRequest();
        request.setUserId(1L);

        Mockito.when(dbQueryService.buscarConFiltros(Mockito.any())).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/user/dbquery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}