package com.crm.BackendCrm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.BackendCrm.dto.Response.FacebookPageResponseDTO;
import com.crm.BackendCrm.dto.Response.ZaloOaResponseDTO;
import com.crm.BackendCrm.security.JwtUtils;
import com.crm.BackendCrm.service.FacebookPageService;
import com.crm.BackendCrm.service.ZaloOaService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@RequestMapping ("/api")
@RestController 
@RequiredArgsConstructor 
public class SocialMediaController {
    private final JwtUtils jwtUtils;
    private final FacebookPageService facebookPageService;
    private final ZaloOaService zaloOaService;

    @GetMapping("/facebookPage")
    public Page<FacebookPageResponseDTO> getFacebookPage(@RequestHeader("Authorization") String authHeader, @RequestParam int page) {
        long userId = getUserIdByAuth(authHeader);

        return facebookPageService.getAllFacePageFromUserId(page, userId);
    }
    @DeleteMapping("/facebookPage/{id}")
    public ResponseEntity deleteFacebookPage(@RequestHeader("Authorization") String authHeader, @PathVariable Long id){
        long userId = getUserIdByAuth(authHeader);

        facebookPageService.delete(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/zaloOa")
    public Page<ZaloOaResponseDTO> getZaloOa(@RequestHeader("Authorization") String authHeader, @RequestParam int page){
        Long userId = getUserIdByAuth(authHeader);

        return zaloOaService.getAllFacePageFromUserId(page, userId);
    }
    @DeleteMapping("/zaloOa/{id}")
    public ResponseEntity deleteZaloOa(@RequestHeader("Authorization") String authHeader, @PathVariable Long id){
        System.out.println("Xóa oa có id: "+id);
        Long userId = getUserIdByAuth(authHeader);

        zaloOaService.delete(id, userId);
        return ResponseEntity.ok().build();
    }

    private Long getUserIdByAuth(String authHeader){
        String jwt = authHeader.substring(7);
        return jwtUtils.extractUserId(jwt);
    }
}
