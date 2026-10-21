package com.crm.BackendCrm.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.crm.BackendCrm.dto.Response.ZaloTokenDTO;
import com.crm.BackendCrm.entity.User;
import com.crm.BackendCrm.entity.ZaloOaAccess;
import com.crm.BackendCrm.entity.ZaloOaRefresh;
import com.crm.BackendCrm.repository.UserRepository;
import com.crm.BackendCrm.repository.ZaloOaAccessRepository;
import com.crm.BackendCrm.repository.ZaloOaRefreshRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConfigOAService {
    // token o day la OA Access Token va OA Refresh Token
    private final ZaloOaAccessRepository zaloOaAccessRepository;
    private final ZaloOaRefreshRepository zaloOaRefreshRepository;
    private final ZaloApiService zaloApiService;
    private final UserRepository userRepository;

    public void configZaloOA(String oauthCode, Long userId, String oaId){
        System.out.println("ConfigOAService bat dau config zalo voi userid:"+userId);
        ZaloTokenDTO zTokenDTO = zaloApiService.getTokenFromCode(oauthCode);

        storeAccessToken(zTokenDTO, userId, oaId);
        storeRefreshToken(zTokenDTO, userId, oaId);
    }

    private void storeAccessToken(ZaloTokenDTO zDto, Long userId, String oaId){
        System.out.println("ConfigOAService Bat dau luu access token");
        User user = getUserFromUserId(userId);

        ZaloOaAccess accessToken = zaloOaAccessRepository.findByOaId(oaId)
        .orElseGet(ZaloOaAccess::new);
        
        accessToken.setAccessToken(zDto.accessToken());
        accessToken.setUserId(userId);
        accessToken.setExpiresIn(zDto.expiresIn()*1000 + System.currentTimeMillis()); // chuyển sang milli giây tính cho dễ
        accessToken.setOaId(oaId);
        accessToken.setCompanyId(user.getCompany().getId());

        zaloOaAccessRepository.save(accessToken);
    }
    private void storeRefreshToken(ZaloTokenDTO zDto, Long userId, String oaId){
        System.out.println("Bat dau luu refresh token");
        User user = getUserFromUserId(userId);

        ZaloOaRefresh refreshToken = zaloOaRefreshRepository.findByOaId(oaId)
            .orElseGet(ZaloOaRefresh::new);

        refreshToken.setOaId(oaId);
        refreshToken.setRefreshToken(zDto.refreshToken());
        refreshToken.setUserId(userId);
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setCompanyId(user.getCompany().getId());

        zaloOaRefreshRepository.save(refreshToken);
    }
    private User getUserFromUserId(Long userId){
        return userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay user co id: "+userId));
    }
}
