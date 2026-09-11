package com.crm.BackendCrm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.BackendCrm.dto.Request.ExportTemplateRequestDTO;
import com.crm.BackendCrm.dto.Response.ExportTemplateResponseDTO;
import com.crm.BackendCrm.entity.ExportTemplate;
import com.crm.BackendCrm.service.ExportTemplateService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import com.crm.BackendCrm.security.JwtUtils;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping ("/api/exportTemplates")
@RequiredArgsConstructor 
@CrossOrigin (origins = "http://localhost:5173")
public class ExportTemplateController {
    private final ExportTemplateService exportTemplateService;
    private final JwtUtils jwtUtils;

    @GetMapping
    public List<ExportTemplateResponseDTO> getAll(@RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdByHeader(authHeader);
        return exportTemplateService.getAllByUserId(userId);
    }
    
    @PostMapping
    public ResponseEntity<ExportTemplateResponseDTO> create(
        @Valid @RequestBody ExportTemplateRequestDTO expt, 
        @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = getUserIdByHeader(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(exportTemplateService.create(expt, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        return ResponseEntity.noContent().build();
    }
    private Long getUserIdByHeader(String authHeader){
        String jwt = authHeader.substring(7);
        Long userId = jwtUtils.extractUserId(jwt);

        return userId;
    }
}
