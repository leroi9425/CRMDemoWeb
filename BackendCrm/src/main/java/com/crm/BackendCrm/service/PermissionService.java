package com.crm.BackendCrm.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.crm.BackendCrm.repository.PermissionRepository;
import com.crm.BackendCrm.entity.Permission;
import com.crm.BackendCrm.dto.Request.PermissionRequestDTO;
import com.crm.BackendCrm.dto.Response.ColumnPermissionResponseDTO;
import com.crm.BackendCrm.dto.Response.PermissionResponseDTO;

// Thư viện Cache của Spring Boot
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    // --- LÁ BÙA 1: CẤT VÀO REDIS ---
    // Lần đầu tiên gọi API, nó chọc DB tính toán Cây, rồi cất kết quả vào Két sắt "permissionTree" trên Redis.
    // Lần thứ 2 trở đi, nó thọc tay vào Redis bốc ra luôn, code dưới này không bị chạy nữa!
    // O(n^2)
    @Cacheable(value = "permissionTree")
    public List<PermissionResponseDTO> getPermissionTree() {
        List<Permission> allPermissions = permissionRepository.findAllByRoles_Name("USER");

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
    public List<ColumnPermissionResponseDTO> getColumnPermissionTree(){
        int numCategory = permissionRepository.countDistinctCategories();
        List<String> listCategory = permissionRepository.findDistinctCategories();

        List<ColumnPermissionResponseDTO> result = new ArrayList<>();

        for (int i=0 ; i<numCategory ; i++){
            List<PermissionResponseDTO> column = getPermissionTreeByCategory(listCategory.get(i));
            ColumnPermissionResponseDTO cpDto = new ColumnPermissionResponseDTO(i, column);
            result.add(cpDto);
        }

        return result;
    }
    // O(2n)
    public List<PermissionResponseDTO> getPermissionTree2(){
        System.out.println("Vẽ cây loại ko đệ quy");
        List<Permission> allPermissions = permissionRepository.findAllByRoles_Name("USER");
        Map<Long, PermissionResponseDTO> permissionMap = new HashMap<>();
        List<PermissionResponseDTO> root = new ArrayList<>();

        for (Permission permission : allPermissions){
            PermissionResponseDTO pDTO = toDTO(permission);
            permissionMap.put(permission.getId(), pDTO);
        }

        for (Permission permission : allPermissions){
            PermissionResponseDTO p = permissionMap.get(permission.getId());

            if (permission.getParentId() == null){
                root.add(p);
            }
            else {
                PermissionResponseDTO parent = permissionMap.get(permission.getParentId());
                if (parent != null)
                    parent.children().add(p);
            }
        }

        return root;
    }
    public List<PermissionResponseDTO> getPermissionTreeByCategory(String category){
        System.out.println("Vẽ cây loại ko đệ quy");
        List<Permission> allPermissions = permissionRepository.findAllByCategoryAndRoles_Name(category, "USER");

        return drawPermissionTree(allPermissions);
    }
    public List<PermissionResponseDTO> drawPermissionTree(List<Permission> allPermissions){
        Map<Long, PermissionResponseDTO> permissionMap = new HashMap<>();
        List<PermissionResponseDTO> root = new ArrayList<>();

        for (Permission permission : allPermissions){
            PermissionResponseDTO pDTO = toDTO(permission);
            permissionMap.put(permission.getId(), pDTO);
        }

        for (Permission permission : allPermissions){
            PermissionResponseDTO p = permissionMap.get(permission.getId());

            if (permission.getParentId() == null){
                root.add(p);
            }
            else {
                PermissionResponseDTO parent = permissionMap.get(permission.getParentId());
                if (parent != null)
                    parent.children().add(p);
            }
        }

        return root;
    }
    public PermissionResponseDTO buildTree2(){
        return null;
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
    private PermissionResponseDTO toDTO(Permission p){
        return new PermissionResponseDTO(
            p.getId(),
            p.getName(),
            p.getDescription(),
            new ArrayList<>()
        );
    }
}
