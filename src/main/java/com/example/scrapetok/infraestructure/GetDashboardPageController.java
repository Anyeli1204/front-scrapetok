package com.example.scrapetok.infraestructure;

import com.example.scrapetok.application.DashboardService;
import com.example.scrapetok.domain.DTO.TopGlobalEmailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
public class GetDashboardPageController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/getDashboardInfo")
    public ResponseEntity<List<TopGlobalEmailDTO>> sendEmail() {
        List<TopGlobalEmailDTO> dto = dashboardService.getData();
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }
}
