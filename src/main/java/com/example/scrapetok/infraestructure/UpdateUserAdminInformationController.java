package com.example.scrapetok.infraestructure;

import com.example.scrapetok.application.UserAdminProfileService;
import com.example.scrapetok.domain.DTO.UpdateInfoDTO;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
public class UpdateUserAdminInformationController {
    @Autowired
    private UserAdminProfileService userAdminProfileService;

    @PatchMapping("/update/{userId}")
    public ResponseEntity<?> update(@PathVariable @NotNull Long userId, @RequestBody UpdateInfoDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(userAdminProfileService.updateProfile(userId, request));
    }
}
