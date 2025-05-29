package com.example.scrapetok.config;

import com.example.scrapetok.application.UserAdminProfileService;
import com.example.scrapetok.domain.DTO.UserProfileResponseDTO;
import com.example.scrapetok.domain.enums.Role;
import com.example.scrapetok.exception.ResourceNotFoundException;
import com.example.scrapetok.infraestructure.UserInformationController;
import com.example.scrapetok.security.JwtRequestFilter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserInformationController.class)
public class UserInformationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserAdminProfileService userAdminProfileService;

    @MockitoBean
    private JwtRequestFilter jwtRequestFilter;

    @Test
    void getUserProfile_Success() throws Exception {
        Long userId = 1L;

        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setId(userId);
        dto.setEmail("email@example.com");
        dto.setFirstname("Juan");
        dto.setLastname("Perez");
        dto.setUsername("juanp");
        dto.setRole(Role.USER);
        dto.setCreationDate(LocalDate.of(2020, 1, 1));
        dto.setAmountScrappedAccount(2);
        dto.setFilters(Collections.emptyList());
        dto.setTiktokUsernameScraped(Set.of("user1", "user2"));

        Mockito.when(userAdminProfileService.getUserProfile(userId)).thenReturn(dto);

        mockMvc.perform(get("/user/profile/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("email@example.com"))
                .andExpect(jsonPath("$.firstname").value("Juan"))
                .andExpect(jsonPath("$.amountScrappedAccount").value(2))
                .andExpect(jsonPath("$.tiktokUsernameScraped").isArray())
                .andExpect(jsonPath("$.tiktokUsernameScraped", Matchers.hasSize(2)));
    }

    @Test
    void getUserProfile_UserNotFound_Returns404() throws Exception {
        Long userId = 999L;

        Mockito.when(userAdminProfileService.getUserProfile(userId))
                .thenThrow(new ResourceNotFoundException("User with id Not Found"));

        mockMvc.perform(get("/user/profile/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))  // error genérico
                .andExpect(jsonPath("$.message").value("User with id Not Found"));  // mensaje personalizado
    }

}