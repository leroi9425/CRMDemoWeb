package com.crm.BackendCrm.controller;

import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController 
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:81"})
public class FacebookWebhookController {
    @GetMapping("")
    public ResponseEntity<String> getWebHook(
        @RequestParam(name = "hub.mode") String mode,
        @RequestParam(name = "hub.verify_token") String token,
        @RequestParam(name = "hub.challenge") String challenge
    ) {
        if(mode.equals("subscribe") && token.equals("crm_test")){
            System.out.println("Nhận webhook");
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("")
    public ResponseEntity<?> postWebHook(@RequestBody String payload) {
        System.out.print(payload);
        return ResponseEntity.ok().build();
    }
    
}
