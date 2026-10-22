package com.example.SocialWorker.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import com.example.SocialWorker.dto.CustomerSocialResponseDTO;
import com.example.SocialWorker.entity.ZaloOaAccess;
import com.example.SocialWorker.entity.ZaloOaRefresh;
import com.example.SocialWorker.exception.NameEmptyException;
import com.example.SocialWorker.exception.ZaloRefreshTokenExpiresException;
import com.example.SocialWorker.exception.ZaloResponseNullException;
import com.example.SocialWorker.exception.ZaloTokenException;
import com.example.SocialWorker.repository.ZaloOaAccessRepository;
import com.example.SocialWorker.repository.ZaloOaRefreshRepository;

@Service 
@RequiredArgsConstructor
public class ZaloService {
    private final ZaloOaAccessRepository zaloOaAccessRepository;
    private final ZaloOaRefreshRepository zaloOaRefreshRepository;
    private final CallApiService callApiService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    // private final String apiCusSocial = "https://webhook.crmviet.vn/api/customer/social";
    // @Value ("${zalo.access.token}")
    // private String zaloToken;

    private String getChatFromMess(JsonNode rootNode){
        return rootNode.path("message").path("text").asText();
    }
    private String getSenderId(JsonNode rootNode){
        return rootNode.path("sender").path("id").asText();
    }
    private String getOaId(JsonNode rootNode){
        return rootNode.path("recipient").path("id").asText();
    }
    private String getNameFromSenderId(String senderId, ZaloOaAccess oAccess){
        //https://openapi.zalo.me/v3.0/oa/user/detail?data=
        // {"user_id":"6056743934778426033"}
        String jsonData = "{\"user_id\":\"" + senderId + "\"}";
        // Cho jsonData vào zaloBody
        JsonNode res = callApiService.getZaloName(Map.of("access_token", oAccess.getAccessToken()), jsonData);
        System.out.println("Kết quả get name zalo: " + res);

        String name = "";
        try{
            name = handleGetNameResponse(res);
            System.out.println("ten cua khach:" + name);
        }
        catch( ZaloResponseNullException e ){
            throw new NameEmptyException("Tên khách bị rỗng");
        }
        catch( ZaloTokenException e ){
            // lấy lại token mới
            String accessToken = getNewAccessToken(oAccess);
            res = callApiService.getZaloName(Map.of("access_token", accessToken), jsonData);
            return handleGetNameResponse(res);
        }
        
        if(name == ""){
            throw new NameEmptyException("Tên khách bị rỗng");
        }

        return name;
    }
    private String handleGetNameResponse(JsonNode res){
        if(res == null){
            throw new ZaloResponseNullException("Response trả về của zalo null");
        }
        int err = res.path("error").asInt(0);
        if(err == 0){
            return res.path("data").path("display_name").asText();
        }
        if(err == -216){
            throw new ZaloTokenException(err, "Access Token bi loi: "+res);
        }
        return "";
    }
    private JsonNode handleZaloTokenResponse(JsonNode res){
        if(res == null)
            throw new ZaloResponseNullException("Response trả về của zalo null: "+res);
        int err = res.path("error").asInt(0);
        if(err == -216){ // xem lai xem ma loi bn
            throw new ZaloRefreshTokenExpiresException("Refresh Token het han: "+res);
        } 

        return res;
    }
    private boolean isAccessTokenExpired(ZaloOaAccess zaloOaAccess){
        if(zaloOaAccess.getExpiresIn() < System.currentTimeMillis())
            return true;
        return false;
    }
    private ZaloOaAccess getAccessByOaId(String oaId){
        return zaloOaAccessRepository.findByOaId(oaId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay access token cho oa id: "+oaId));
    }
    private ZaloOaRefresh getRefreshByOaId(String oaId){
        return zaloOaRefreshRepository.findByOaId(oaId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay refresh token cho oa id: "+oaId));
    }
    private String getNewAccessToken(ZaloOaAccess oldAccess){
        String oaId = oldAccess.getOaId();
        ZaloOaRefresh refresh = getRefreshByOaId(oaId);
        JsonNode res = callApiService.postToGetZaloToken(refresh.getRefreshToken());
        JsonNode zaloTokens = handleZaloTokenResponse(res); 

        storeAccessToken(zaloTokens, oldAccess.getUserId(), oldAccess.getCompanyId(), oaId);
        storeRefreshToken(zaloTokens, oldAccess.getUserId(), oldAccess.getCompanyId(), oaId);

        return zaloTokens.path("access_token").asText();
    }
    // viết hàm gọi api đến backendcrm để lấy lại cặp token mới
    //......... viết ở đây ..............
    public CustomerSocialResponseDTO handleOAChat(JsonNode rootNode){
        String textChat = getChatFromMess(rootNode);
        String senderId = getSenderId(rootNode);
        String oaId = getOaId(rootNode);

        String phoneNumber = TextChatService.getPhoneNumberFromTextChat(textChat);
        String email = TextChatService.getEmailFromTextChat(textChat);
        if(phoneNumber == null && email == null){
            return null;
        }

        ZaloOaAccess zaloOaAccess = getAccessByOaId(oaId);
        if(isAccessTokenExpired(zaloOaAccess)){
            String newAccessToken = getNewAccessToken(zaloOaAccess);

            zaloOaAccess.setAccessToken(newAccessToken);
        }

        String cusName = getNameFromSenderId(senderId, zaloOaAccess);

        if(cusName.isEmpty()){
            String newAccessToken = getNewAccessToken(zaloOaAccess);
            zaloOaAccess.setAccessToken(newAccessToken);
            cusName = getNameFromSenderId(senderId, zaloOaAccess);
        }
        if(cusName.isEmpty()){
            throw new NameEmptyException("Lỗi tên khách bị rỗng");
        }

        CustomerSocialResponseDTO cDto = new CustomerSocialResponseDTO(
            cusName,
            phoneNumber,
            email,
            zaloOaAccess.getUserId(),
            zaloOaAccess.getCompanyId()
        );

        return cDto;
    }
    private void storeAccessToken(JsonNode zaloTokens, Long userId, Long companyId, String oaId){
        System.out.println("ConfigOAService Bat dau luu access token");

        ZaloOaAccess accessToken = zaloOaAccessRepository.findByOaId(oaId)
        .orElseGet(ZaloOaAccess::new);
        
        accessToken.setAccessToken(zaloTokens.path("access_token").asText());
        accessToken.setUserId(userId);
        accessToken.setExpiresIn(zaloTokens.path("expires_in").asLong() *1000 + System.currentTimeMillis()); // chuyển sang milli giây tính cho dễ
        accessToken.setOaId(oaId);
        accessToken.setCompanyId(companyId);

        zaloOaAccessRepository.save(accessToken);
    }
    private void storeRefreshToken(JsonNode zaloTokens, Long userId, Long companyId, String oaId){
        System.out.println("Bat dau luu refresh token");

        ZaloOaRefresh refreshToken = zaloOaRefreshRepository.findByOaId(oaId)
            .orElseGet(ZaloOaRefresh::new);

        refreshToken.setOaId(oaId);
        refreshToken.setRefreshToken(zaloTokens.path("access_token").asText());
        refreshToken.setUserId(userId);
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setCompanyId(companyId);

        zaloOaRefreshRepository.save(refreshToken);
    }
    public void handleZaloWebHook(JsonNode rootNode){
        System.out.println("Bắt đầu xử lý zalo Webhook");
        CustomerSocialResponseDTO cDto;
        cDto = handleOAChat(rootNode);
        
        if (cDto != null) {
            System.out.println("Hoàn tất xử lý, chuẩn bị DTO để cho crm" + cDto);
            callApiService.postCustomerSocialDTOToCrm(cDto);
        }
        else{
            System.out.println("Lỗi do json cDto null");
        }
        
        System.out.println("Hoàn tất xử lý zalo Webhook");
    }
}
