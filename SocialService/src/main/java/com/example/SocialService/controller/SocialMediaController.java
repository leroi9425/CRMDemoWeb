package com.example.SocialService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SocialService.service.SocialMediaService;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController 
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
@CrossOrigin (origins = "http://localhost:5173")
public class SocialMediaController {
    private final SocialMediaService socialMediaService;

    @PostMapping
    public ResponseEntity<?> getWebHook(@RequestBody JsonNode data) {
        System.out.println("START-socialMediaController/getWebHook");
        System.out.println(data);
        System.out.println("END-socialMediaController/getWebHook");
        return socialMediaService.create(data);
    }

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
    
}
