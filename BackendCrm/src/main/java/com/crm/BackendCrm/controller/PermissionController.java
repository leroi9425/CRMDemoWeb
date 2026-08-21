package com.crm.BackendCrm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;

import com.crm.BackendCrm.service.PermissionService;
import com.crm.BackendCrm.dto.AuthRequest;
import com.crm.BackendCrm.dto.AuthResponse;
import com.crm.BackendCrm.dto.PermissionRequestDTO;
import com.crm.BackendCrm.dto.PermissionResponseDTO;
import com.crm.BackendCrm.service.RoleService;

import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController    
@RequestMapping("/api/permissions")
@RequiredArgsConstructor   
@CrossOrigin(origins = "http://localhost:5173")  
public class PermissionController {
    
    private final PermissionService permissionService;
    private final RoleService roleService;

    // API 1: Trả về JSON cây quyền cho Frontend
    @GetMapping("/tree")
    public ResponseEntity<List<PermissionResponseDTO>> getTree() {
        List<PermissionResponseDTO> tree = permissionService.getPermissionTree();
        return ResponseEntity.ok(tree);
    }

    // API 2: Thêm quyền mới
    @PostMapping("/create")
    public ResponseEntity<?> createPermission(@Valid @RequestBody PermissionRequestDTO requestDTO) {
        permissionService.createPermission(requestDTO);
        return ResponseEntity.ok("Thêm mới quyền thành công!");
    }
}
