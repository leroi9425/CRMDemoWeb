package com.crm.BackendCrm.service;

import org.springframework.stereotype.Service;

import com.crm.BackendCrm.dto.Response.ZaloTokenDTO;

@Service
public class ZaloApiService {
    public ZaloTokenDTO getTokenFromCode(String oathCode, String appId, String codeVerifier){
        
        return null;
    }
    public ZaloTokenDTO getTokenFromRefreshToken(){
        return null;
    }
}
