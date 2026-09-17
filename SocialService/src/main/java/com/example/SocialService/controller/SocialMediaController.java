package com.example.SocialService.controller;



import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SocialService.service.SocialMediaService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
@CrossOrigin (origins = "http://localhost:5173")
public class SocialMediaController {
    private final SocialMediaService socialMediaService;

    @PostMapping
    public ResponseEntity<?> getWebHook(@RequestBody String data) {
        System.out.println("SocialMediaController : "+new Date());
        System.out.println(data);
        System.out.println("End SocialMediaController");
        return socialMediaService.create(data);
    }
    
}
