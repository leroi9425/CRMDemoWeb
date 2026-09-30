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

    private String getChatFromMess(JsonNode rootNode){
        return rootNode.path("message").path("text").asText();
    }
    private String getSenderId(JsonNode rootNode){
        return rootNode.path("sender").path("id").asText();
    }
    private String getNameFromSenderId(String senderId){
        String api = zaloApi + senderId + "" + zaloToken;
        JsonNode res = DtoService.getDTO(api);
        return res.path("name").asText();
    }

    public void handleZaloWebHook(JsonNode rootNode){
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
        DtoService.postDTO(cDto, apiCusSocial);
    }
}
