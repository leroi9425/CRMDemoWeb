package com.example.SocialWorker.service;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import com.example.SocialWorker.dto.CustomerSocialResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DtoService {
    public static void postDTO(CustomerSocialResponseDTO csDto, String api){
        if(csDto != null){
            RestClient client = RestClient.create();
            String result = client.post()
                    .uri(api)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(csDto)
                    .retrieve()
                    .body(String.class);
            System.out.println("Kết quả gửi cusDTO: "+ result);
        }
    }
    public static JsonNode getDTO(String api, Map<String, String> headers, String zaloBody){
        RestClient client = RestClient.create();
        System.out.println("=== getDTO ===");
        System.out.println("api = " + api);
        System.out.println("headers = " + headers);
        System.out.println("zaloBody = " + zaloBody);
        String result = null;
        if(zaloBody == null){
            result = client.method(HttpMethod.GET)
                .uri(URI.create(api))
                .headers((h) -> headers.forEach(h::add))
                .retrieve()
                .body(String.class);
        }
        else{
            result = client.method(HttpMethod.GET)
                .uri(URI.create(api))
                .headers((h) -> headers.forEach(h::add))
                .body(zaloBody)
                .retrieve()
                .body(String.class);
        }
        System.out.println("Kết quả gửi cusDTO: "+ result);
        try {
            
            ObjectMapper obm = new ObjectMapper();
            JsonNode rootNode = obm.readTree(result);

            return  rootNode;
        } catch (Exception e) {
            System.out.println("service/DtoService/getDTO bị lỗi: " + e);
        }
        return null;
    }
}
