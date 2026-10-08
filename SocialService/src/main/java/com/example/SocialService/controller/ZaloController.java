package com.example.SocialService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping("/api/zalo")
@RequiredArgsConstructor
public class ZaloController {
    @PostMapping("/connect")
    public ResponseEntity<?> connect(@RequestBody String entity) {
        RestClient restClient = RestClient.create();
        // String resul = restClient.get()
            // .uri()
            // .
        
        return ResponseEntity.ok().build();
    }
    
}
