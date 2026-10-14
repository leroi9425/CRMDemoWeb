package com.crm.BackendCrm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.BackendCrm.security.JwtUtils;
import com.crm.BackendCrm.service.ConfigPageService;
import com.crm.BackendCrm.service.FacebookApiService;
import com.crm.BackendCrm.service.OathStateService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;



@RestController 
@RequestMapping("/api/facebook")
@RequiredArgsConstructor
public class ConfigPageController {
    private final ConfigPageService configPageService;
    private final FacebookApiService facebookApiService;
    private final JwtUtils jwtUtils;
    private final OathStateService oathStateService;

    @GetMapping("/connect")
    public Map<String, String> connect(@RequestHeader("Authorization") String authHeader) {
        String jwt = authHeader.substring(7);
        Long userId = jwtUtils.extractUserId(jwt);

        String state = oathStateService.createAndSaveStateWithUserId(userId);

        String url = facebookApiService.facebookConnect(state);
        return Map.of("url", url);
    }
    
    @GetMapping("/redirect")    // facebook gọi về tg này
    public void  redirectUrl(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws IOException {
        Long userId = oathStateService.getUserIdFromState(state);
        configPageService.configFacebookPage(code, userId);
        
        response.sendRedirect("https://webhook.crmviet.vn");
    }
    
}
