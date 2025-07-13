package com.example.scrapetok.infraestructure;

import com.example.scrapetok.application.JwtService;
import com.example.scrapetok.domain.DTO.LoginRequestDTO;
import com.example.scrapetok.domain.GeneralAccount;
import com.example.scrapetok.repository.GeneralAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private GeneralAccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        GeneralAccount user = accountRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        String token = jwtService.generateToken(user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("access_token", token);
        response.put("expires_in", jwtService.getExpirationMillis());
        response.put("token_type", "Bearer");
        response.put("issued_at", new Date());

        return ResponseEntity.ok(response);
    }
}
