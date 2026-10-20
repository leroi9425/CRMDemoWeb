package com.crm.BackendCrm.service;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.crm.BackendCrm.dto.Response.ZaloTokenDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ZaloApiService {
    @Value("${APP_ZALO_SECRET}")
    private String secretKey;
    @Value("${APP_ZALO_ID}")
    private String appId;
    private RestClient client = RestClient.create();
    private ObjectMapper objectMapper = new ObjectMapper();
    private String redirectUri = "https://webhook.crmviet.vn/api/zalo/redirect";

    public String oaConntectWithState(String state){
        System.out.println("ZaloAPiService bat dau truy cap url de connect OA");
        return UriComponentsBuilder.fromUriString("https://oauth.zaloapp.com/v4/oa/permission")
                .queryParam("app_id", appId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", state)
                .build()
                .encode()
                .toUriString();
    }

    public ZaloTokenDTO getTokenFromCode(String oauthCode){
        System.out.println("ZaloAPiService lay token bang code");
        MultiValueMap <String, String> body = new LinkedMultiValueMap<>();
        body.add("code", oauthCode);
        body.add("app_id", appId);
        body.add("grant_type", "authorization_code");

        String response = getResponse(body);

        return handleResponse(response);
    }
    public ZaloTokenDTO getTokenFromRefreshToken(String refreshToken){
        System.out.println("ZaloAPiService lay token tu refresh token "+refreshToken);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("refresh_token", refreshToken);
        body.add("app_id",appId);
        body.add("grant_type", "refresh_token");

        String response = getResponse(body);

        return handleResponse(response);
    }
    private ZaloTokenDTO handleResponse(String response){
        System.out.println("ZaloAPiService xu ly response "+response);
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(response);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi chuyển hóa JSON từ Zalo", e);
        }

        if(!jsonNode.hasNonNull("access_token"))
            throw new IllegalStateException("Zalo không trả access_token: " + jsonNode);

        String accessToken = jsonNode.path("access_token").asText();
        String refreshToken = jsonNode.path("refresh_token").asText();
        Long expiresIn = jsonNode.path("expires_in").asLong();

        return new ZaloTokenDTO(accessToken, refreshToken, expiresIn);
    }
    private String getResponse(MultiValueMap <String, String> body){
        System.out.println("ZaloAPiService nhan response");
        return client.post()
            .uri("https://oauth.zaloapp.com/v4/oa/access_token")
            .header("secret_key", secretKey)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .body(String.class);
    }
}
