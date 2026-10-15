package com.example.SocialWorker.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.SocialWorker.dto.CustomerSocialResponseDTO;
import com.example.SocialWorker.entity.FacebookPage;
import com.example.SocialWorker.repository.FacebookPageRepository;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class FacebookService {
    // private final String apiCusSocial = "http://localhost:8089/api/customer/social";
    private final String apiCusSocial = "http://localhost:8089/api/customer/social";
    private final String faceApi = "https://graph.facebook.com/v26.0/";
    @Value("${facebook.access.token}")
    private String facebookToken;
    private final FacebookPageRepository facebookPageRepository;

    public String getChatFromComment(JsonNode entryNode){
        return entryNode.path("changes").get(0).path("value").path("message").asText();
    }
    public String getNameFromComment(JsonNode entryNode){
        return entryNode.path("changes").get(0).path("value").path("from").path("name").asText();
    }
    public String getSenderId(JsonNode entryNode){
        return entryNode.path("messaging").get(0).path("sender").path("id").asText();
    }
    public String getPageId(JsonNode entryNode){
        return entryNode.path("id").asText();
    }
    public String getNameFromFaceId(String faceId, String pageId, String longPageAccessToken){
        // String api = faceApi + faceId + "?fields=name&access_token=" + facebookToken;
        System.out.println("Lấy tên từ facebookId: " + faceId);
        System.out.println("Lấy tên từ pageId: " + pageId);
        String api = faceApi + faceId + "?fields=name";
        JsonNode cusInfo = DtoService.getDTO(api, Map.of("Authorization", "Bearer " + longPageAccessToken), null);
        return cusInfo.path("name").asText();
    }
    public String getChatFromMess(JsonNode entryNode){
        return entryNode.path("messaging").get(0).path("message").path("text").asText();
    }
    
    public void handleFaceWebHook(JsonNode rootNode){
        System.out.println("Vào hàm xử lý facebook");
        String chatContext = null;
        String cusName = null;
        String phoneNumber = null;
        String email = null;

        JsonNode entryNode = rootNode.path("entry").get(0);
        String pageId = getPageId(entryNode);
        List<FacebookPage> facebookPages = facebookPageRepository.findAllByPageId(pageId);        
        // có thể báo lỗi k tìm thấy page ở đây
        for (FacebookPage facebookPage : facebookPages) {
            try {
            if(entryNode.has("changes")){
                // comment
                chatContext = getChatFromComment(entryNode);
                cusName = getNameFromComment(entryNode);
            }
            else if(entryNode.has("messaging")){
                // tin nhan
                chatContext = getChatFromMess(entryNode);
                String senderId = getSenderId(entryNode);

                cusName = getNameFromFaceId(senderId, pageId, facebookPage.getLongPageAccessToken());
            }

            System.out.println("Lấy thử chatcontext facebookService: " +chatContext);
            // trích xuất email, số điện thoại
            if(chatContext != null &&!chatContext.isEmpty()){
                phoneNumber = TextChatService.getPhoneNumberFromTextChat(chatContext);
                email = TextChatService.getEmailFromTextChat(chatContext);
                System.out.println("Lấy thử số và email trong facebookService: " +phoneNumber+email);
            }
            if(phoneNumber == null && email == null){
                return;
            }
            // kẹp vòng for để lấy 1 lúc 2 ô khách hàng
            CustomerSocialResponseDTO csDTO = new CustomerSocialResponseDTO(
                cusName,
                phoneNumber,
                email,
                facebookPage.getUserId(),
                facebookPage.getCompanyId()
            );
            System.out.println("CusDTO: " + csDTO);
            DtoService.postDTO(csDTO, apiCusSocial);

            } catch (Exception e) {
                System.out.print("Loi khi xu ly JSON: " + e);
            }
        }
    }
}
