package com.example.scrapetok.infraestructure;

import com.example.scrapetok.application.DBQueryService;
import com.example.scrapetok.domain.DTO.UserDBQueryRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyRole('USER')")
@RequestMapping("/user")
public class DataBaseQueriesController {
    @Autowired
    private DBQueryService dbQueryService;

    @PostMapping("dbquery")
    public ResponseEntity<?> makeDbQueries(@RequestBody @Valid UserDBQueryRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(dbQueryService.buscarConFiltros(request));
    }
}
