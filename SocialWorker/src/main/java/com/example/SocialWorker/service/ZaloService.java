package com.example.SocialWorker.service;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.SocialWorker.dto.CustomerSocialResponseDTO;
import com.example.SocialWorker.service.TextChatService;;

@Service 
public class ZaloService {
    private final String zaloApi = "tmp";
    private final String zaloToken = "lay tu properties";
    private final String apiCusSocial = "http://localhost:8089/api/customer/social";
    // private final String apiCusSocial = "https://webhook.crmviet.vn/api/customer/social";

    private String getChatFromMess(JsonNode rootNode){
        return rootNode.path("message").path("text").asText();
    }
    private String getSenderId(JsonNode rootNode){
        return rootNode.path("sender").path("id").asText();
    }
    private String getNameFromSenderId(String senderId){
        String api = zaloApi + senderId + "" + zaloToken;
        // JsonNode res = DtoService.getDTO(api);
        // String name = res.path("name").asText();
        return "zalo name";
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
