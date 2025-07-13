package com.example.scrapetok.infraestructure;

import com.example.scrapetok.application.DashboardService;
import com.example.scrapetok.application.TopGlobalEmailService;
import com.example.scrapetok.domain.DTO.TopGlobalEmailDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/admin")
public class TopGlobalEmailController {
    @Autowired
    private TopGlobalEmailService topGlobalEmailService;
    @Autowired
    private DashboardService dashboardService;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/sendemail")
    public ResponseEntity<String> sendEmail(@RequestBody @Valid List<TopGlobalEmailDTO> request) {
        dashboardService.publishData(request);
        topGlobalEmailService.sendTopGlobalTextEmail(request);
        return ResponseEntity.status(HttpStatus.OK).body("✅ Top daily global emails have been sent successfully and has been published\"");
    }

}
