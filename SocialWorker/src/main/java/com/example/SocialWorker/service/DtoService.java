package com.example.SocialWorker.service;

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
    public static JsonNode getDTO(String api){
        RestClient client = RestClient.create();
        String result = client.get()
                .uri(api)
                .retrieve()
                .body(String.class);
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
