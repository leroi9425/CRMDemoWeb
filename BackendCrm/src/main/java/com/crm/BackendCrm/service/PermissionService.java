package com.crm.BackendCrm.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.crm.BackendCrm.repository.PermissionRepository;
import com.crm.BackendCrm.entity.Permission;
import com.crm.BackendCrm.dto.Request.PermissionRequestDTO;
import com.crm.BackendCrm.dto.Response.ColumnPermissionResponseDTO;
import com.crm.BackendCrm.dto.Response.PermissionResponseDTO;

// Thư viện Cache của Spring Boot
// import org.springframework.cache.annotation.Cacheable;
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
    // @Cacheable(value = "permissionTree")
    // public List<PermissionResponseDTO> getPermissionTree() {
    //     List<Permission> allPermissions = permissionRepository.findAllByRoles_Name("USER");

    //     return allPermissions.stream()
    //             .filter(p -> p.getParentName() == null) 
    //             .map(root -> buildTree(root, allPermissions)) 
    //             .collect(Collectors.toList());
    // }

    // private PermissionResponseDTO buildTree(Permission parent, List<Permission> allPermissions) {
    //     List<PermissionResponseDTO> children = allPermissions.stream()
    //             .filter(p -> parent.getName().equals(p.getParentName()))
    //             .map(child -> buildTree(child, allPermissions))
    //             .collect(Collectors.toList());

    //     return new PermissionResponseDTO(
    //             parent.getId(),
    //             parent.getName(),
    //             parent.getDescription(),
    //             false, // isChecked
    //             children
    //     );
    // }


    // --- Sửa N+1 query
    public List<ColumnPermissionResponseDTO> getPermissionTree(String roleName) {
        // List<Permission> allPermissions = permissionRepository.findAllByRoles_Name(roleName);
        List<Permission> allPermissions = permissionRepository.findAllByRoles_Name(roleName);
        Map<String, List<Permission>> permissionMap = allPermissions.stream()
                .collect(Collectors.groupingBy(Permission::getCategory));
        List<ColumnPermissionResponseDTO> result = new ArrayList<>();
        int columnIndex = 0;
        for (List<Permission> permissions : permissionMap.values()) {
            ColumnPermissionResponseDTO column = new ColumnPermissionResponseDTO(columnIndex, drawPermissionTree(permissions));
            result.add(column);
            columnIndex++;
        }
        return result;        
    }

    // ===========================
    // Hàm cho lệnh tạo cây quyền theo cột 
    // ===========================

    // vẽ cây theo cột
    // cài list vào đoạn này, hàm sau chỉ cần gọi list
    public List<ColumnPermissionResponseDTO> getColumnPermissionTree(String roleName){
        // Tạo list category
        List<String> listCategory = permissionRepository.findDistinctCategories();
        List<ColumnPermissionResponseDTO> result = new ArrayList<>();
        for (int i=0 ; i<listCategory.size() ; i++){
            List<PermissionResponseDTO> column = getPermissionTreeByCategoryAndRoleName(listCategory.get(i), roleName);
            ColumnPermissionResponseDTO cpDto = new ColumnPermissionResponseDTO(i, column);
            result.add(cpDto);
        }
        return result;
    }
    public List<PermissionResponseDTO> getPermissionTreeByCategoryAndRoleName(String category, String roleName){
        System.out.println("Vẽ cây loại ko đệ quy");
        List<Permission> allPermissions = permissionRepository.findAllByCategoryAndRoles_Name(category, roleName);
        return drawPermissionTree(allPermissions);
    }
    private Map<String, PermissionResponseDTO> buildPermissionMap(List<Permission> allPermissions) {
        Map<String, PermissionResponseDTO> permissionMap = new HashMap<>();
        for (Permission permission : allPermissions) {
            PermissionResponseDTO pDTO = toDTO(permission);
            permissionMap.put(permission.getName(), pDTO); // key = parentName, value = permissionDTO
        }
        return permissionMap;
    }
    private  List<PermissionResponseDTO> buildTreeFromMap(Map<String, PermissionResponseDTO> permissionMap, List<Permission> allPermissions){
        List<PermissionResponseDTO> root = new ArrayList<>();

        for (Permission permission : allPermissions){
            PermissionResponseDTO p = permissionMap.get(permission.getName());
            if (permission.getParentName() == null){
                System.out.println("PermissionService: phát hiện node cha: "+p.name());
                root.add(p);
            }
            else {
                PermissionResponseDTO parent = permissionMap.get(permission.getParentName());
                System.out.println("PermissionService: phát hiện node con: "+p.name()+" có cha là: "+parent.name());
                if (parent != null)
                    parent.children().add(p);
            }
        }

        return root;
    }
    public List<PermissionResponseDTO> drawPermissionTree(List<Permission> allPermissions){
        Map<String, PermissionResponseDTO> permissionMap = buildPermissionMap(allPermissions);
        List<PermissionResponseDTO> root = buildTreeFromMap(permissionMap, allPermissions);

        return root;
    }

    // --- BỎ CACHE CŨ ---
    // Khi Admin thêm Quyền mới, cái cây cũ trên Redis bị sai. 
    // Lá bùa này sẽ tự động ra lệnh ĐỐT cháy cái Két sắt "permissionTree" trên Redis.
    @CacheEvict(value = "permissionTree", allEntries = true)
    public Permission createPermission(PermissionRequestDTO dto) {
        Permission permission = new Permission();
        permission.setName(dto.name());
        permission.setDescription(dto.description());
        permission.setParentName(dto.parentName());
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
            false, // isChecked
            new ArrayList<>()
        );
    }
}
