package com.crm.BackendCrm.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.BackendCrm.security.JwtUtils;
import com.crm.BackendCrm.service.ConfigOAService;
import com.crm.BackendCrm.service.OathStateService;
import com.crm.BackendCrm.service.ZaloApiService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("api/zalo")
@RequiredArgsConstructor
public class ConfigOAController {
    private final JwtUtils jwtUtils;
    private final OathStateService oathStateService;
    private final ConfigOAService configOAService;
    private final ZaloApiService zaloApiService;

    @GetMapping("connect")
    public Map<String, String> connect(@RequestHeader("Authorization") String authHeader) {
        String jwt = authHeader.substring(7);
        Long userId = jwtUtils.extractUserId(jwt);

        String state = oathStateService.createAndSaveStateWithUserId(userId);
        String url = zaloApiService.oaConntectWithState(state);

        return Map.of("url", url);
    }

    @GetMapping("/redirect")
    public void redirectUrl(@RequestParam String code, @RequestParam String state, @RequestParam String oaId, HttpServletResponse response) throws IOException {
        Long userId = oathStateService.getUserIdFromState(state);
        configOAService.configZaloOA(code, userId, oaId);

        response.sendRedirect("https://webhook.crmviet.vn");
    }
    
}
