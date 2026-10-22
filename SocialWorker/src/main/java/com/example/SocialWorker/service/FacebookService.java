package com.example.SocialWorker.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.SocialWorker.dto.CustomerSocialResponseDTO;
import com.example.SocialWorker.entity.FacebookPage;
import com.example.SocialWorker.exception.FacebookPermissionException;
import com.example.SocialWorker.exception.FacebookResponseNullException;
import com.example.SocialWorker.exception.FacebookTokenExpiredException;
import com.example.SocialWorker.exception.NameEmptyException;
import com.example.SocialWorker.repository.FacebookPageRepository;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class FacebookService {
    @Value("${facebook.access.token}")
    private String facebookToken;
    private final FacebookPageRepository facebookPageRepository;
    private final CallApiService callApiService;

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
    public String getChatFromMess(JsonNode entryNode){
        return entryNode.path("messaging").get(0).path("message").path("text").asText();
    }

    public String getNameFromSenderId(String senderId, String longPageAccessToken){
        // String api = faceApi + faceId + "?fields=name&access_token=" + facebookToken;
        System.out.println("Lấy tên từ facebookId cua sendr: " + senderId);
        JsonNode res = callApiService.getFaceName(Map.of("Authorization", "Bearer " + longPageAccessToken), senderId);
        JsonNode cusInfo = handleResponse(res);
        String name = cusInfo.path("name").asText();
        if(name.isEmpty())
            throw new NameEmptyException("Tên của khách lấy từ senderId facebook bị rỗng");
        return cusInfo.path("name").asText();
    }
    private JsonNode handleResponse(JsonNode res){
        if(res == null)
            throw new FacebookResponseNullException("Response rỗng khi nhận response từ Facebook");

        int err = res.path("error").path("code").asInt(0);
        if (err == 0)
            return res;
        if (err == 3)
            throw new FacebookPermissionException(
                "Vấn đề về tính năng hoặc quyền. Đảm bảo ứng dụng của bạn có chức năng hoặc quyền cần thiết để thực hiện lệnh gọi này." + 
                res.path("error").path("message").asText()
            );
        if (err == 190){
            throw new FacebookTokenExpiredException("Token hết hạn hãy lấy token mới");
        }
        return res;
    }
    public String getPhoneNumberFromComment(JsonNode entryNode){
        String chatContext = getChatFromComment(entryNode);
        if(chatContext != null &&!chatContext.isEmpty()){
            return TextChatService.getPhoneNumberFromTextChat(chatContext);
        }
        return null;
    }
    public String getPhoneNumberFromMess(JsonNode entryNode){
        String chatContext = getChatFromMess(entryNode);
        if(chatContext != null &&!chatContext.isEmpty()){
            return TextChatService.getPhoneNumberFromTextChat(chatContext);
        }
        return null;
    }
    public String getEmailFromComment(JsonNode entryNode){
        String chatContext = getChatFromMess(entryNode);
        if(chatContext != null &&!chatContext.isEmpty()){
            return TextChatService.getEmailFromTextChat(chatContext);
        }
        return null;
    }
    public String getEmailFromMess(JsonNode entryNode){
        String chatContext = getChatFromMess(entryNode);
        if(chatContext != null &&!chatContext.isEmpty()){
            return TextChatService.getEmailFromTextChat(chatContext);
        }
        return null;
    }
    public CustomerSocialResponseDTO handleCommentFromNode(JsonNode entryNode, FacebookPage facebookPage) {       
        String cusName = getNameFromComment(entryNode);
        String phoneNumber = getPhoneNumberFromComment(entryNode);
        String email = getEmailFromComment(entryNode);
        System.out.println("Lấy thử số và email trong facebookService: " +phoneNumber+email);

        if(phoneNumber == null && email == null){
            return null;
        }

        CustomerSocialResponseDTO csDTO = new CustomerSocialResponseDTO(
            cusName,
            phoneNumber,
            email,
            facebookPage.getUserId(),
            facebookPage.getCompanyId()
        );

        return csDTO;
    }
    public CustomerSocialResponseDTO handleMessageFromNode(JsonNode entryNode, FacebookPage facebookPage){
        String senderId = getSenderId(entryNode);
        String cusName = getNameFromSenderId(senderId, facebookPage.getLongPageAccessToken());
        String phoneNumber = getPhoneNumberFromMess(entryNode);
        String email = getEmailFromMess(entryNode);
        System.out.println("Lấy thử số và email trong facebookService: " +phoneNumber+email);

        if(phoneNumber == null && email == null){
            return null;
        }

        CustomerSocialResponseDTO csDTO = new CustomerSocialResponseDTO(
            cusName,
            phoneNumber,
            email,
            facebookPage.getUserId(),
            facebookPage.getCompanyId()
        );

        return csDTO;
    }
    
    public void handleFaceWebHook(JsonNode rootNode){
        System.out.println("Vào hàm xử lý facebook");

        JsonNode entryNode = rootNode.path("entry").get(0);
        if(entryNode == null){
            System.out.println("Không có entry node");
            return;
        }

        String pageId = getPageId(entryNode);
        List<FacebookPage> facebookPages = facebookPageRepository.findAllByPageId(pageId);
        if(facebookPages.isEmpty()){
            System.out.println("Không tìm thấy FacebookPage với pageId: " + pageId);
        }

        String typeJson;      
        // có thể báo lỗi k tìm thấy page ở đây
        for (FacebookPage facebookPage : facebookPages) {
            if(entryNode.has("changes"))
                typeJson = "comment";
            else if(entryNode.has("messaging"))
                typeJson = "messaging";
            else
                typeJson = "unknown";

            CustomerSocialResponseDTO csDTO;

            switch (typeJson) {
                case "comment":
                    csDTO = handleCommentFromNode(entryNode, facebookPage);
                    break;
                case "messaging":
                    csDTO = handleMessageFromNode(entryNode, facebookPage);
                    break;
                default:
                    csDTO = null;
                    break;
            }
            
            // nhúm json ra rồi gửi luôn cho BackenCrm
            if(csDTO != null){
                System.out.println("CusDTO: " + csDTO);
                callApiService.postCustomerSocialDTOToCrm(csDTO);
            }
            else{
                System.out.println("Khách hàng null");
            }
        }
    }
}
