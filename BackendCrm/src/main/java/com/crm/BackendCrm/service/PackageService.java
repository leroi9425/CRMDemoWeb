package com.crm.BackendCrm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.crm.BackendCrm.dto.Request.PackageRequestDTO;
import com.crm.BackendCrm.dto.Response.ColumnPermissionResponseDTO;
import com.crm.BackendCrm.dto.Response.PackageDetailResponseDto;
import com.crm.BackendCrm.dto.Response.PackageResponseDTO;
import com.crm.BackendCrm.dto.Response.PermissionResponseDTO;
import com.crm.BackendCrm.entity.Package;
import com.crm.BackendCrm.entity.Permission;
import com.crm.BackendCrm.repository.PackageRepository;
import com.crm.BackendCrm.repository.PermissionRepository;

import lombok.RequiredArgsConstructor;;

@Service
@RequiredArgsConstructor
public class PackageService {
    private final PackageRepository packageRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionService permissionService;

    public List<PackageResponseDTO> getAllPackage(){
        return packageRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }
    public PackageDetailResponseDto getDetailPackage(Long packageId){
        System.out.println("PackageService: chuẩn bị xem chi tiết gói");
        Package p = packageRepository.findById(packageId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy gói với id:"+packageId));

        List<String> checkNameList = new ArrayList<>(p.getPermissions().stream().map(Permission::getName).collect(Collectors.toList()));

        List<ColumnPermissionResponseDTO> tree = permissionService.getPermissionTreeChecked("USER", checkNameList);

        return new PackageDetailResponseDto(
            p.getId(),
            p.getPrice(),
            p.getName(),
            p.getDescription(),
            p.getStatus(),
            tree
        );
    }
    public PackageResponseDTO create(PackageRequestDTO pDto){
        if(isExitsPermissionNotForUser(pDto)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (isExitsPackage(pDto)){
           throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        
        // 4.1.3.12
        // nên viết procedure hay là code ?
        // thử code trước
        List<Permission> permissions = permissionRepository.findAllByNameIn(pDto.listPermissionNames());
        Package newPack = new Package();
        newPack.setName(pDto.name());
        newPack.setPrice(pDto.price());
        newPack.setDescription(pDto.description());
        newPack.setStatus(pDto.status());
        newPack.setPermissions(new HashSet<>(permissions));

        packageRepository.save(newPack);
        
        return toDTO(newPack);
    }
    public PackageResponseDTO update(PackageRequestDTO pDto, Long packageId){
        if(isExitsPermissionNotForUser(pDto)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (isExitsPackage(pDto)){
           throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        Package pack = packageRepository.findById(packageId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy gói với id:"+packageId));
        List<Permission> permissions = permissionRepository.findAllByNameIn(pDto.listPermissionNames());
        pack.setName(pDto.name());
        pack.setPrice(pDto.price());
        pack.setDescription(pDto.description());
        pack.setStatus(pDto.status());
        pack.setPermissions(new HashSet<>(permissions));

        packageRepository.save(pack);

        return toDTO(pack);
    }
    public void delete(Long packageId){
        System.out.println("PackageService: chuẩn bị xóa gói");
        packageRepository.deleteById(packageId);
    }
    private boolean isExitsPermissionNotForUser(PackageRequestDTO pDto){
        List<Permission> listUserPermissions = permissionRepository.findAllByRoles_Name("USER");
        Set<String> setPermissionDto = new HashSet<>(pDto.listPermissionNames());
        Set<String> setPermissionDB = new HashSet<>(
            listUserPermissions
            .stream()
            .map(Permission::getName)
            .collect(Collectors.toSet())
        );
        // thêm dấu ! cho phù hợp ngữ cảnh của hàm
        return !setPermissionDB.containsAll(setPermissionDto);
    }
    private boolean isExitsPackage(PackageRequestDTO pDto){
        List<Object[]> listPackagePermission = packageRepository.getAllPackagePermission();
        List<String> listPermissionFromDto = pDto.listPermissionNames();

        // Map với key = packageId, value = list permissionName
        Map<Long, List<String>> packagePermission = getMapFromListObj(listPackagePermission);
        boolean isExistPackage = packagePermission
                                .values()
                                .stream()
                                .anyMatch(
                                    listPermissionName ->
                                    new HashSet<>(listPermissionFromDto).equals(new HashSet<>(listPermissionName))
                                );
        return isExistPackage;
    }
    private Map<Long, List<String>> getMapFromListObj(List<Object[]> listPackagePermission){
        Map<Long, List<String>> mapPackagePermission = new HashMap<>();
        for(Object[] pp : listPackagePermission){
            Long packageId = ((Number) pp[0]).longValue();
            String permissionName = (String) pp[1];

            List<String> getListPermission = mapPackagePermission.get(packageId);
            if (getListPermission == null){
                getListPermission = new ArrayList<>();
            }

            getListPermission.add(permissionName);
            mapPackagePermission.put(packageId, getListPermission);
        }
        return mapPackagePermission;
    }

    private PackageResponseDTO toDTO(Package p) {
        return new PackageResponseDTO(
            p.getId(),
            p.getPrice(),
            p.getName(),
            p.getDescription(),
            p.getStatus()
        );
    }
}
