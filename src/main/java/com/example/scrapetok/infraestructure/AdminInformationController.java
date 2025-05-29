package com.example.scrapetok.infraestructure;

import com.example.scrapetok.application.UserAdminProfileService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminInformationController {
    @Autowired
    private UserAdminProfileService userAdminProfileService;

    @GetMapping("/profile/{adminId}")
    public ResponseEntity<?> profile(@PathVariable @NotNull Long adminId) {
        return ResponseEntity.status(HttpStatus.OK).body(userAdminProfileService.getAdminProfile(adminId));
    }
}
