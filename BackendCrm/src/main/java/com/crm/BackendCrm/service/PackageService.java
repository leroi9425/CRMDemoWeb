package com.crm.BackendCrm.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bouncycastle.crypto.util.Pack;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.crm.BackendCrm.dto.Request.PackageRequestDTO;
import com.crm.BackendCrm.dto.Response.PackageDetailResponseDto;
import com.crm.BackendCrm.dto.Response.PackageResponseDTO;
import com.crm.BackendCrm.dto.Response.PermissionResponseDTO;
import com.crm.BackendCrm.entity.Package;
import com.crm.BackendCrm.entity.Permission;
import com.crm.BackendCrm.repository.PackageRepository;
import com.crm.BackendCrm.repository.PermissionRepository;
import com.rabbitmq.client.RpcClient.Response;

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
        Package p = packageRepository.findById(packageId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy gói với id:"+packageId));

        List<Permission> packPermission = new ArrayList<>(p.getPermissions());
        List<PermissionResponseDTO> tree = permissionService.drawPermissionTree(packPermission);

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
        // nên viết procedure hay là code  ?
        // thử code trước
        List<Permission> permissions = permissionRepository.findAllById(pDto.listPermissionId());
        Package newPack = new Package();
        newPack.setName(pDto.name());
        newPack.setPrice(pDto.price());
        newPack.setDescription(pDto.description());
        newPack.setStatus(pDto.status());
        newPack.setPermissions(new HashSet<>(permissions));

        packageRepository.save(newPack);
        
        return toDTO(newPack);
    }
    private boolean isExitsPermissionNotForUser(PackageRequestDTO pDto){
        List<Permission> listUserPermissions = permissionRepository.findAllByRoles_Name("USER");
        Set<Long> setPermissionDto = new HashSet<>(pDto.listPermissionId());
        Set<Long> setPermissionDB = new HashSet<>(
            listUserPermissions
            .stream()
            .map(Permission::getId)
            .collect(Collectors.toSet())
        );
        // thêm dấu ! cho phù hợp ngữ cảnh của hàm
        return !setPermissionDB.containsAll(setPermissionDto);
    }
    private boolean isExitsPackage(PackageRequestDTO pDto){
        List<Object[]> listPackagePermission = packageRepository.getAllPackagePermission();
        List<Long> listPermissionFromDto = pDto.listPermissionId();

        Map<Long, List<Long>> packagePermission = getMapFromListObj(listPackagePermission);
        boolean isExistPackage = packagePermission
                                .values()
                                .stream()
                                .anyMatch(
                                    listPermissionId ->
                                    new HashSet<>(listPermissionFromDto).equals(new HashSet<>(listPermissionId))
                                );
        return isExistPackage;
    }
    private Map<Long, List<Long>> getMapFromListObj(List<Object[]> listPackagePermission){
        Map<Long, List<Long>> mapPackagePermission = new HashMap<>();
        for(Object[] pp : listPackagePermission){
            Long packageId = ((Number) pp[0]).longValue();
            Long permissionId = ((Number) pp[1]).longValue();

            List<Long> getListPermission = mapPackagePermission.get(packageId);
            if (getListPermission == null){
                getListPermission = new ArrayList<>();
            }

            getListPermission.add(permissionId);
            mapPackagePermission.put(packageId, getListPermission);
        }
        return mapPackagePermission;
    }

    private PackageResponseDTO toDTO(Package p){
        return new PackageResponseDTO(
            p.getId(),
            p.getPrice(),
            p.getName(),
            p.getDescription(),
            p.getStatus()
        );
    }
}
