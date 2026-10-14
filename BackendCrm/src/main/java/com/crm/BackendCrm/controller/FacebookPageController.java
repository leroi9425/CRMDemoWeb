package com.crm.BackendCrm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.BackendCrm.dto.Response.FacebookPageResponseDTO;
import com.crm.BackendCrm.security.JwtUtils;
import com.crm.BackendCrm.service.FacebookPageService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@RequestMapping ("/api/facebookPage")
@RestController 
@RequiredArgsConstructor 
public class FacebookPageController {
    private final JwtUtils jwtUtils;
    private final FacebookPageService facebookPageService;

    @GetMapping("")
    public Page<FacebookPageResponseDTO> getFacebookPage(@RequestHeader("Authorization") String authHeader, @RequestParam int page) {
        String jwt = authHeader.substring(7);
        long userId = jwtUtils.extractUserId(jwt);

        return facebookPageService.getAllFacePageFromUserId(page, userId);
    }
    
}
