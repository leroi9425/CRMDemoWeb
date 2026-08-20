package com.crm.BackendCrm.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.crm.BackendCrm.repository.PermissionRepository;
import com.crm.BackendCrm.entity.Permission;
import com.crm.BackendCrm.dto.PermissionRequestDTO;
import com.crm.BackendCrm.dto.PermissionResponseDTO;

// Thư viện Cache của Spring Boot
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    // --- LÁ BÙA 1: CẤT VÀO REDIS ---
    // Lần đầu tiên gọi API, nó chọc DB tính toán Cây, rồi cất kết quả vào Két sắt "permissionTree" trên Redis.
    // Lần thứ 2 trở đi, nó thọc tay vào Redis bốc ra luôn, code dưới này không bị chạy nữa!
    @Cacheable(value = "permissionTree")
    public List<PermissionResponseDTO> getPermissionTree() {
        List<Permission> allPermissions = permissionRepository.findAll();

        return allPermissions.stream()
                .filter(p -> p.getParentId() == null) 
                .map(root -> buildTree(root, allPermissions)) 
                .collect(Collectors.toList());
    }

    private PermissionResponseDTO buildTree(Permission parent, List<Permission> allPermissions) {
        List<PermissionResponseDTO> children = allPermissions.stream()
                .filter(p -> parent.getId().equals(p.getParentId()))
                .map(child -> buildTree(child, allPermissions))
                .collect(Collectors.toList());

        return new PermissionResponseDTO(
                parent.getId(),
                parent.getName(),
                parent.getDescription(),
                children
        );
    }

    // --- LÁ BÙA 2: ĐỐT CHÁY CACHE CŨ ---
    // Khi Admin thêm Quyền mới, cái cây cũ trên Redis bị sai. 
    // Lá bùa này sẽ tự động ra lệnh ĐỐT cháy cái Két sắt "permissionTree" trên Redis.
    @CacheEvict(value = "permissionTree", allEntries = true)
    public Permission createPermission(PermissionRequestDTO dto) {
        Permission permission = new Permission();
        permission.setName(dto.name());
        permission.setDescription(dto.description());
        permission.setParentId(dto.parentId());
        return permissionRepository.save(permission);
    }

    public Permission getById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
    }
    
    public Permission getByName(String name) {
        return permissionRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Permission not found with name: " + name));
    }
}
