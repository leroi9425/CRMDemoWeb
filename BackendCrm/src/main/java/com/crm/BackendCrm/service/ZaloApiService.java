package com.crm.BackendCrm.service;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.crm.BackendCrm.dto.Response.ZaloTokenDTO;
import com.crm.BackendCrm.exception.ZaloAccessTokenException;
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

    public String getOaName(String accessToken){
        System.out.println("ZaloAPiService bat dau truy cap url de lay thon tin OA de lay ten OA");
        
        String res = callApiOaGetOa(accessToken);
        return getNameFromRes(res);
    }

    public ZaloTokenDTO getTokenFromCode(String oauthCode){
        System.out.println("ZaloAPiService lay token bang code");
        MultiValueMap <String, String> body = new LinkedMultiValueMap<>();
        body.add("code", oauthCode);
        body.add("app_id", appId);
        body.add("grant_type", "authorization_code");

        String response = callApiOaAccessToken(body);

        return handleTokenResponse(response);
    }
    public ZaloTokenDTO getTokenFromRefreshToken(String refreshToken){
        System.out.println("ZaloAPiService lay token tu refresh token "+refreshToken);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("refresh_token", refreshToken);
        body.add("app_id",appId);
        body.add("grant_type", "refresh_token");

        String response = callApiOaAccessToken(body);

        return handleTokenResponse(response);
    }
    private ZaloTokenDTO handleTokenResponse(String response){
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
    private String getNameFromRes(String res){
        try {
            JsonNode oaInfo = objectMapper.readTree(res);
            int err = oaInfo.path("error").asInt(0);
            if (err == -216)
                throw new ZaloAccessTokenException("Access token hết hạn hoặc lỗi, hãy lấy lại token mới "+ res);
            if (err == 0)
                return oaInfo.path("data").path("name").asText();
            else
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Zalo trả lỗi: " + oaInfo.path("message").asText());
        } catch (Exception e) {
            System.out.println("lỗi khi lấy tên từ response: "+e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Zalo trả lỗi: " + e);
        }
    }
    private String callApiOaGetOa(String accessToken){
        return client.get()
            .uri("https://openapi.zalo.me/v2.0/oa/getoa")
            .header("access_token", accessToken)
            .retrieve()
            .body(String.class);
    }
    private String callApiOaAccessToken(MultiValueMap <String, String> body){
        System.out.println("ZaloAPiService nhan response tu method post");
        return client.post()
            .uri("https://oauth.zaloapp.com/v4/oa/access_token")
            .header("secret_key", secretKey)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .body(String.class);
    }
}
