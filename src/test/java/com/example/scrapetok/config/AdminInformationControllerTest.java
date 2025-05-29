package com.example.scrapetok.config;

import com.example.scrapetok.application.UserAdminProfileService;
import com.example.scrapetok.exception.ResourceNotFoundException;
import com.example.scrapetok.infraestructure.AdminInformationController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AdminInformationController.class)
public class AdminInformationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserAdminProfileService userAdminProfileService;

    // Si en tu configuración de seguridad tienes un filtro de JWT, mockéalo también:
    @MockitoBean
    private com.example.scrapetok.security.JwtRequestFilter jwtRequestFilter;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void getAdminProfile_NotFound_Returns404() throws Exception {
        Long adminId = 999L;

        when(userAdminProfileService.getAdminProfile(adminId))
                .thenThrow(new ResourceNotFoundException("Admin with id not found"));

        mockMvc.perform(get("/admin/profile/{adminId}", adminId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Admin with id not found"));
    }
}