package com.example.SocialService.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping("/api/facebook")
@RequiredArgsConstructor
@CrossOrigin (origins = "http://localhost:5173")
public class FacebookControllerAuth {
    @Value("${APP_ID}")
    private String appId;

    final private String redirectUri = "https://webhook.crmviet.vn";

    @GetMapping("/connect")
    public Map<String, String> connect() {
        String url = UriComponentsBuilder.fromUriString("https://www.facebook.com/v26.0/dialog/oauth")
                .queryParam("client_id", appId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "pages_show_list,pages_messaging,pages_read_engagement,pages_manage_metadata")
                .build()
                .encode()
                .toUriString();
        
        return Map.of("url", url);
    }
    
}
