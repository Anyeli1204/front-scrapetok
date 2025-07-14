package com.example.scrapetok.infraestructure;

import com.example.scrapetok.application.IAService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@Data
@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class ChatController {
    @Autowired
    private IAService iaService;

    @PostMapping("/ia/chat/idea1")
    public ResponseEntity<?> chat1(@RequestBody Map<String, String> request) {
        Map<String,String> response1 = iaService.chat1(request);
        return ResponseEntity.ok(response1);
    }

    @GetMapping("/ia/chat/idea2")
    public ResponseEntity<Map<String, String>> chat2() {
        Map<String,String> response2 = iaService.chat2();
        return ResponseEntity.ok(response2);
    }

    @PostMapping("/ia/chat/idea3")
    public ResponseEntity<Map<String, String>> chat3(@RequestBody Map<String, String> request) {
        Map<String,String> response3 = iaService.chat3(request);
        return ResponseEntity.ok(response3);
    }
}