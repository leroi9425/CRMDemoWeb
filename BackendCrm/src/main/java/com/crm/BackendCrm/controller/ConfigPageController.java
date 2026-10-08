package com.crm.BackendCrm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.crm.BackendCrm.service.ConfigPageService;
import com.crm.BackendCrm.service.FacebookApiService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;



@RestController 
@RequestMapping("/api/facebook")
@RequiredArgsConstructor
public class ConfigPageController {
    private final ConfigPageService configPageService;
    private final FacebookApiService facebookApiService;

    @GetMapping("/connect")
    public Map<String, String> connect() {
        String url = facebookApiService.facebookConnect();
        return Map.of("url", url);
    }
    
    @GetMapping("/redirect")    // facebook gọi về tg này
    public String redirectUrl(@RequestParam String code) {
        configPageService.configFacebookPage(code);
        
        return new String();
    }
    
}
