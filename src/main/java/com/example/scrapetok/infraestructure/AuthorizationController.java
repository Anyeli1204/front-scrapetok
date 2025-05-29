package com.example.scrapetok.infraestructure;

import com.example.scrapetok.application.AuthorizationService;
import com.example.scrapetok.domain.DTO.LoginRequestDTO;
import com.example.scrapetok.domain.DTO.LoginResponseDTO;
import com.example.scrapetok.domain.DTO.UpgradeToAdminRequestDTO;
import com.example.scrapetok.domain.DTO.UserSignUpRequestDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    @Autowired
    private AuthorizationService authorizationService;


    @PostMapping("/signup")
    public ResponseEntity<?> userRegistration(@RequestBody @Valid UserSignUpRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorizationService.createUser(request));
    }


    @PostMapping("/signupadmin")
    public ResponseEntity<?> adminRegistration(@RequestBody @Valid UserSignUpRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorizationService.createAdmin(request));
    }

    // Falta SignIn
    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO request) {
        LoginResponseDTO response = authorizationService.login(request);
        return ResponseEntity.ok(response);
    }


    // Admin concede rol de admin a User
    @PatchMapping("/upgradetoadmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> upgradeToAdmin(@RequestBody @Valid UpgradeToAdminRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorizationService.upgrade(request));
    }
}
