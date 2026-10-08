package com.example.SocialWorker.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.example.SocialWorker.dto.CustomerSocialResponseDTO;
import com.example.SocialWorker.service.TextChatService;;

@Service 
public class ZaloService {
    private final String zaloApi = "https://openapi.zalo.me/v3.0/oa/user/detail";
    private final String apiCusSocial = "http://localhost:8089/api/customer/social";
    // private final String apiCusSocial = "https://webhook.crmviet.vn/api/customer/social";
    @Value ("${zalo.access.token}")
    private String zaloToken;

    private String getChatFromMess(JsonNode rootNode){
        return rootNode.path("message").path("text").asText();
    }
    private String getSenderId(JsonNode rootNode){
        return rootNode.path("sender").path("id").asText();
    }
    // private String getNameFromSenderId(String senderId){
    //     //https://openapi.zalo.me/v3.0/oa/user/detail?data=
    //     // {"user_id":"6056743934778426033"}
    //     String jsonData = "{\"user_id\":\"" + senderId + "\"}";
    //     // do zl gửi có kẹp json string vào url nên phải encode riêng
    //     String encodedData = URLEncoder.encode(jsonData, StandardCharsets.UTF_8);
    //     String api = zaloApi + "?data=" + encodedData; 

    //     System.out.println("JSON DATA: " + jsonData);
    //     System.out.println("ENCODED DATA: " + encodedData);
    //     System.out.println("API: " + api);

    //     JsonNode res = DtoService.getDTO(api, Map.of("access_token", zaloToken));
    //     System.out.println("Kết quả get name zalo: " + res);
    //     String name = res.path("data").path("display_name").asText();
    //     return name;
    // }
    private String getNameFromSenderId(String senderId){
        //https://openapi.zalo.me/v3.0/oa/user/detail?data=
        // {"user_id":"6056743934778426033"}
        String jsonData = "{\"user_id\":\"" + senderId + "\"}";
        // do zl gửi có kẹp json string vào url nên phải encode riêng

        JsonNode res = DtoService.getDTO(zaloApi, Map.of("access_token", zaloToken), jsonData);
        System.out.println("Kết quả get name zalo: " + res);
        String name = res.path("data").path("display_name").asText();
        return name;
    }

    public void handleZaloWebHook(JsonNode rootNode){
        System.out.println("Bắt đầu xử lý zalo Webhook");
        try {
            String textChat = getChatFromMess(rootNode);
            String senderId = getSenderId(rootNode);

            String phoneNumber = TextChatService.getPhoneNumberFromTextChat(textChat);
            String email = TextChatService.getEmailFromTextChat(textChat);

            if(phoneNumber == null && email == null){
                return;
            }
            System.out.println("Số điện thoại và email có 1 trong 2 đi tiếp");
            String cusName = getNameFromSenderId(senderId);

            // tao dto
            CustomerSocialResponseDTO cDto = new CustomerSocialResponseDTO(
                cusName,
                phoneNumber,
                email
            );
            System.out.println("Hoàn tất xử lý, chuẩn bị DTO để cho crm" + cDto);
            DtoService.postDTO(cDto, apiCusSocial);
            
            System.out.println("Hoàn tất xử lý zalo Webhook");
        } catch (Exception e) {
            System.out.println("Lỗi khi xử lý Zalo Webhook");
            System.out.println(e);
        }
        
    }
}
