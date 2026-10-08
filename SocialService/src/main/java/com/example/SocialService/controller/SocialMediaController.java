package com.example.SocialService.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SocialService.service.HashService;
import com.example.SocialService.service.SocialMediaService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

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

    @Value("${OA_SECRET}")
    private String OA_SECRET; 
    @Value("${APP_SECRET}")
    private String FACEBOOK_SECRET;

    @PostMapping
    public ResponseEntity<?> getWebHook(HttpServletRequest request, @RequestBody String body) throws NoSuchAlgorithmException {
        System.out.println("START-socialMediaController/getWebHook/facebook");
        System.out.println("START-socialMediaController/getWebHook/facebook: body" + body);
        String facebookSignature = request.getHeader("X-Hub-Signature-256");

        if(facebookSignature == null || facebookSignature.isBlank()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Facebook signature");
        }
        if(facebookSignature.startsWith("sha256=")){
            facebookSignature = facebookSignature.substring(7);
        }

        String mySignature = HashService.hmacSHA256(FACEBOOK_SECRET, body);
        if(!mySignature.equals(facebookSignature)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Facebook signature");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode data = objectMapper.readTree(body);

        return socialMediaService.createFacebookJson(data);
    }
    @PostMapping("/zalo")
    public ResponseEntity<?> getWebHookZalo(HttpServletRequest request, @RequestBody String body) throws NoSuchAlgorithmException  {
        String zaloSignature = request.getHeader("X-ZEvent-Signature");
        if(zaloSignature == null || zaloSignature.isBlank()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Zalo signature");
        }
        if(zaloSignature.startsWith("mac=")){
            zaloSignature = zaloSignature.substring(4);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode data = objectMapper.readTree(body);
        
        String appId = data.path("app_id").asText();
        String timestamp = data.path("timestamp").asText();

        String rawData = appId + body + timestamp + OA_SECRET;

        String serverSignature = HashService.hashSHA256(rawData);

        if(!serverSignature.equals(zaloSignature)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Zalo signature");
        }
        System.out.println("START-socialMediaController/getWebHook/zalo");
        System.out.println(data);
        System.out.println("END-socialMediaController/getWebHook/zalo");
        return socialMediaService.createZaloJson(data);
    }

    @GetMapping("")
    public ResponseEntity<String> getWebHook(
        @RequestParam(name = "hub.mode") String mode,
        @RequestParam(name = "hub.verify_token") String token,
        @RequestParam(name = "hub.challenge") String challenge
    ) {
        System.out.println("===== META VERIFY =====");
        System.out.println("mode = [" + mode + "]");
        System.out.println("token = [" + token + "]");
        System.out.println("challenge = [" + challenge + "]");
        System.out.println("=======================");
        if(mode.equals("subscribe") && token.equals("crm_test")){
            System.out.println("Nhận webhook");
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
}
