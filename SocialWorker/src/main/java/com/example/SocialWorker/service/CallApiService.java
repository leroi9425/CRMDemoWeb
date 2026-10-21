package com.example.SocialWorker.service;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.example.SocialWorker.dto.CustomerSocialResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CallApiService {
    private final String zaloApi = "https://openapi.zalo.me/v3.0/oa/user/detail";
    private final String apiCusSocial = "http://localhost:8089/api/customer/social";
    private final String faceApi = "https://graph.facebook.com/v26.0/";
    private final RestClient client = RestClient.create();
    private final ObjectMapper obm = new ObjectMapper();
    @Value("${APP_ZALO_ID}")
    private String appZaloId;
    @Value("${APP_ZALO_SECRET}")
    private String zaloSecretKey;

    public void postCustomerSocialDTOToCrm(CustomerSocialResponseDTO csDto){
        if(csDto != null){
            RestClient client = RestClient.create();
            String result = client.post()
                    .uri(apiCusSocial)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(csDto)
                    .retrieve()
                    .body(String.class);
            System.out.println("Kết quả gửi cusDTO: "+ result);
        }
    }
    // hàm này viết sau + chép từ backendcrm xuống nên logic khác với các hàm khác
    public JsonNode postToGetZaloToken(String refreshToken){
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("refresh_token", refreshToken);
        body.add("app_id",appZaloId);
        body.add("grant_type", "refresh_token");

        String response = client.post()
            .uri("https://oauth.zaloapp.com/v4/oa/access_token")
            .header("secret_key", zaloSecretKey)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .body(String.class);
        
        try {
            JsonNode json = obm.readTree(response);
            return json;
        } catch (Exception e) {
            System.out.println("service/DtoService/getDTO bị lỗi: " + e);
        }
        return null;
    }
    // gửi method get đến api của zalo/facebook, thường dùng để lấy tên khách hàng
    public JsonNode getFaceName(Map<String, String> headers, String faceId){
        String api = faceApi + faceId + "?fields=name";

        System.out.println("=== getDTO ===");
        System.out.println("api = " + api);
        System.out.println("headers = " + headers);

        String result = client.method(HttpMethod.GET)
            .uri(URI.create(api))
            .headers((h) -> headers.forEach(h::add))
            .retrieve()
            .body(String.class);
        System.out.println("Kết quả gửi cusDTO: "+ result);
        try {
            JsonNode rootNode = obm.readTree(result);

            return  rootNode;
        } catch (Exception e) {
            System.out.println("service/DtoService/getDTO bị lỗi: " + e);
        }
        return null;
    }
    public JsonNode getZaloName(Map<String, String> headers, String zaloBody){
        System.out.println("=== getDTO ===");
        System.out.println("api = " + zaloApi);
        System.out.println("headers = " + headers);
        System.out.println("zaloBody = " + zaloBody);
        String result = client.method(HttpMethod.GET)
                .uri(URI.create(zaloApi))
                .headers((h) -> headers.forEach(h::add))
                .body(zaloBody)
                .retrieve()
                .body(String.class);
        System.out.println("Kết quả gửi cusDTO: "+ result);
        try {
            JsonNode rootNode = obm.readTree(result);

            return  rootNode;
        } catch (Exception e) {
            System.out.println("service/DtoService/getDTO bị lỗi: " + e);
        }
        return null;
    }
}
