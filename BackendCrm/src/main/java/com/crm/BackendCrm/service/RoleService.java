package com.crm.BackendCrm.service;

import com.crm.BackendCrm.dto.RolePermissionRequestDTO;
import com.crm.BackendCrm.entity.Permission;
import com.crm.BackendCrm.entity.Role;
import com.crm.BackendCrm.repository.PermissionRepository;
import com.crm.BackendCrm.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    // Lấy danh sách ID các quyền mà Role đang có (để bôi đen checkbox trên FE)
    @Cacheable(value = "rolePermissions", key = "#roleId")
    public List<Long> getRolePermissions(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Role với ID: " + roleId));
        
        return role.getPermissions().stream()
                .map(Permission::getId)
                .collect(Collectors.toList());
    }

    // Phục vụ cho JwtAuthFilter check quyền siêu tốc
    @Cacheable(value = "rolePermsByName", key = "#roleName")
    public List<String> getPermissionNamesByRoleName(String roleName) {
        Role role = roleRepository.findAll().stream()
                .filter(r -> r.getName().equals(roleName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Role: " + roleName));
        return role.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());
    }

    // Cập nhật lại toàn bộ quyền cho Role khi Admin ấn Lưu
    @Transactional
    @org.springframework.cache.annotation.Caching(evict = {
        @CacheEvict(value = "rolePermissions", key = "#roleId"),
        @CacheEvict(value = "rolePermsByName", allEntries = true)
    })
    public void assignPermissionsToRole(Long roleId, RolePermissionRequestDTO dto) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Role với ID: " + roleId));

        // Nếu danh sách ID truyền lên rỗng, thì xóa hết quyền
        if (dto.permissionIds() == null || dto.permissionIds().isEmpty()) {
            role.getPermissions().clear();
        } else {
            // Chọc DB lấy các Entity Permission tương ứng với mảng ID
            List<Permission> permissions = permissionRepository.findAllById(dto.permissionIds());
            
            // Xóa sạch quyền cũ và nhét nguyên mảng quyền mới vào
            role.setPermissions(new HashSet<>(permissions));
        }

        // Lưu lại (Hibernate sẽ tự động DROP và INSERT vào bảng permission_role)
        roleRepository.save(role);
        
        // Lưu ý: Nếu sau này bạn có Cache dữ liệu quyền của User lúc Login,
        // bạn sẽ phải gọi Redis xóa Cache của tất cả User thuộc Role này ở đây!
    }

    public List<com.crm.BackendCrm.dto.RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(r -> new com.crm.BackendCrm.dto.RoleDTO(r.getId(), r.getName(), r.getDescription()))
                .collect(Collectors.toList());
    }

    public com.crm.BackendCrm.dto.RoleDTO getRoleById(Long id) {
        Role r = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        return new com.crm.BackendCrm.dto.RoleDTO(r.getId(), r.getName(), r.getDescription());
    }

    @Transactional
    public com.crm.BackendCrm.dto.RoleDTO createRole(com.crm.BackendCrm.dto.RoleDTO dto) {
        Role role = new Role();
        role.setName(dto.name());
        role.setDescription(dto.description());
        role = roleRepository.save(role);
        return new com.crm.BackendCrm.dto.RoleDTO(role.getId(), role.getName(), role.getDescription());
    }

    @Transactional
    public com.crm.BackendCrm.dto.RoleDTO updateRole(Long id, com.crm.BackendCrm.dto.RoleDTO dto) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        role.setName(dto.name());
        role.setDescription(dto.description());
        role = roleRepository.save(role);
        return new com.crm.BackendCrm.dto.RoleDTO(role.getId(), role.getName(), role.getDescription());
    }
}
