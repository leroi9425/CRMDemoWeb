package com.crm.BackendCrm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.BackendCrm.dto.Request.CustomerSocialRequestDTO;
import com.crm.BackendCrm.service.CustomerSocialService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/api/customer") 
@RequiredArgsConstructor 
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:81"})
public class CustomerSocialController {
    private final CustomerSocialService customerSocialService;

    @PostMapping("/social")
    public ResponseEntity<String> createCustomer(@Valid @RequestBody CustomerSocialRequestDTO c) {
        customerSocialService.reviceCustomer(c);
        return ResponseEntity.ok().build();
    }
}
