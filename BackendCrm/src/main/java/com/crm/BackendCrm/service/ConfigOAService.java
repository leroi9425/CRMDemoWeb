package com.crm.BackendCrm.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.crm.BackendCrm.dto.Response.ZaloTokenDTO;
import com.crm.BackendCrm.entity.ZaloOaAccess;
import com.crm.BackendCrm.entity.ZaloOaRefresh;
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

    public void configZaloOA(String oauthCode, Long userId, String oaId){
        System.out.println("ConfigOAService bat dau config zalo voi userid:"+userId);
        ZaloTokenDTO zTokenDTO = zaloApiService.getTokenFromCode(oauthCode);

        storeAccessToken(zTokenDTO, userId, oaId);
        storeRefreshToken(zTokenDTO, userId, oaId);
    }

    private void storeAccessToken(ZaloTokenDTO zDto, Long userId, String oaId){
        System.out.println("ConfigOAService Bat dau luu access token");
        ZaloOaAccess accessToken = new ZaloOaAccess();
        accessToken.setAccessToken(zDto.accessToken());
        accessToken.setUserId(userId);
        accessToken.setExpiresIn(zDto.expiresIn());
        accessToken.setOaId(oaId);

        zaloOaAccessRepository.save(accessToken);
    }
    private void storeRefreshToken(ZaloTokenDTO zDto, Long userId, String oaId){
        System.out.println("Bat dau luu refresh token");
        ZaloOaRefresh refreshToken = new ZaloOaRefresh();
        refreshToken.setOaId(oaId);
        refreshToken.setRefreshToken(zDto.refreshToken());
        refreshToken.setUserId(userId);
        refreshToken.setCreatedAt(LocalDateTime.now());

        zaloOaRefreshRepository.save(refreshToken);
    }
}
