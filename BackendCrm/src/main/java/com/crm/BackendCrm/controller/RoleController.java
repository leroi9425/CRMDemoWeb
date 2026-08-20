package com.crm.BackendCrm.controller;

import com.crm.BackendCrm.dto.RolePermissionRequestDTO;
import com.crm.BackendCrm.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class RoleController {

    private final RoleService roleService;

    // API lấy mảng ID quyền của 1 Role
    @GetMapping("/{id}/permissions")
    public ResponseEntity<List<Long>> getRolePermissions(@PathVariable Long id) {
        List<Long> permissionIds = roleService.getRolePermissions(id);
        return ResponseEntity.ok(permissionIds);
    }

    // API cập nhật (gán) quyền cho Role
    @PostMapping("/{id}/permissions")
    public ResponseEntity<?> assignPermissions(@PathVariable Long id, @Valid @RequestBody RolePermissionRequestDTO dto) {
        roleService.assignPermissionsToRole(id, dto);
        return ResponseEntity.ok("Cập nhật quyền cho nhóm thành công!");
    }

    @GetMapping
    public ResponseEntity<List<com.crm.BackendCrm.dto.RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<com.crm.BackendCrm.dto.RoleDTO> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PostMapping
    public ResponseEntity<com.crm.BackendCrm.dto.RoleDTO> createRole(@RequestBody com.crm.BackendCrm.dto.RoleDTO dto) {
        return ResponseEntity.ok(roleService.createRole(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<com.crm.BackendCrm.dto.RoleDTO> updateRole(@PathVariable Long id, @RequestBody com.crm.BackendCrm.dto.RoleDTO dto) {
        return ResponseEntity.ok(roleService.updateRole(id, dto));
    }
}
