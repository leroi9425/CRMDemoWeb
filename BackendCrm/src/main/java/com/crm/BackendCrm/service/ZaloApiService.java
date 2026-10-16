package com.crm.BackendCrm.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.crm.BackendCrm.dto.Response.ZaloTokenDTO;

@Service
public class ZaloApiService {
    public ZaloTokenDTO getTokenFromCode(String oathCode, String appId, String codeVerifier){
        RestClient restclient = RestClient.create();
        
        return null;
    }
    public ZaloTokenDTO getTokenFromRefreshToken(){

        return null;
    }
}
