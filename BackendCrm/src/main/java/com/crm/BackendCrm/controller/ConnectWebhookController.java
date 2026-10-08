package com.crm.BackendCrm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;



@RestController 
@RequestMapping("/api/facebook")
@RequiredArgsConstructor
public class ConnectWebhookController {
    @Value("${APP_ID}")
    private String appId;
    @Value ("${APP_SECRET}")
    private String appSecret;

    final private String redirectUri = "https://webhook.crmviet.vn/api/facebook/redirect";

    @GetMapping("/connect")
    public Map<String, String> connect() {
        System.out.println("Kết nối Facebook Page ConnectWebhookController");
        System.out.println("APP_ID: " + appId);
        System.out.println("REDIRECT_URI: " + redirectUri);
        String url = UriComponentsBuilder.fromUriString("https://www.facebook.com/v26.0/dialog/oauth")
                .queryParam("client_id", appId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "pages_show_list,pages_messaging,pages_read_engagement,pages_manage_metadata")
                .build()
                .encode()
                .toUriString();
        System.out.println("URL kết nối Facebook Page: " + url);
        System.out.println("Kết thúc kết nối Facebook Page ConnectWebhookController và return");
        return Map.of("url", url);
    }
    
    @GetMapping("/redirect")    // facebook gọi về tg này
    public String redirectUrl(@RequestParam String code) {
        System.out.println("Nhận được code từ Facebook: " + code);
        RestClient client = RestClient.create();
        String accessTokenUrl = UriComponentsBuilder
                .fromUriString("https://graph.facebook.com/v26.0/oauth/access_token")
                .queryParam("client_id", appId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("client_secret", appSecret)
                .queryParam("code", code)
                .build()
                .encode()
                .toUriString();
        System.out.println("URL lấy access token: " + accessTokenUrl);

        String accessTokenResponse = client.get()
                .uri(accessTokenUrl)
                .retrieve()
                .body(String.class);
        // Thực hiện yêu cầu để lấy access token
        System.out.println("Access token response: " + accessTokenResponse);
        
        return new String();
    }
    
}
