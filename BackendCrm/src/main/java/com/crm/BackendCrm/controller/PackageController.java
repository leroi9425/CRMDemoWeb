package com.crm.BackendCrm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.crm.BackendCrm.dto.Request.PackageRequestDTO;
import com.crm.BackendCrm.dto.Response.PackageResponseDTO;
import com.crm.BackendCrm.security.JwtUtils;
import com.crm.BackendCrm.service.PackageService;
import com.crm.BackendCrm.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequiredArgsConstructor
@RequestMapping("api/packages")
public class PackageController {
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final PackageService packageService;
    @GetMapping()
    public List<PackageResponseDTO> getMethodName(@RequestHeader("Authorization") String header) {
        Long userId = getUserIdByAuth(header);
        if(!userService.isAdmin(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "bạn không có quyền thực hiện lệnh này");
        }
        return packageService.getAllPackage();
    }
    @PostMapping("")
    public PackageResponseDTO createPack(@Valid @RequestBody PackageRequestDTO pDto) {
        return packageService.create(pDto);
    }
    
    
    private Long getUserIdByAuth(String authHeader){
        String jwt = authHeader.substring(7);
        return jwtUtils.extractUserId(jwt);
    }
}
