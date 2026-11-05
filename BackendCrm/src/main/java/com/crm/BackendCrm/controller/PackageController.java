package com.crm.BackendCrm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.crm.BackendCrm.dto.Request.PackageRequestDTO;
import com.crm.BackendCrm.dto.Response.ColumnPermissionResponseDTO;
import com.crm.BackendCrm.dto.Response.PackageDetailResponseDto;
import com.crm.BackendCrm.dto.Response.PackageResponseDTO;
import com.crm.BackendCrm.entity.User;
import com.crm.BackendCrm.repository.UserRepository;
import com.crm.BackendCrm.security.JwtUtils;
import com.crm.BackendCrm.service.PackageService;
import com.crm.BackendCrm.service.PermissionService;
import com.crm.BackendCrm.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/packages")
public class PackageController {
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final PackageService packageService;
    private final PermissionService permissionService;
    private final UserRepository userRepository;
    
    @GetMapping()
    public List<PackageResponseDTO> getAllPackage(@RequestHeader("Authorization") String header) {
        System.out.println("PackageController: chuẩn bị xem quyền");
        // Long userId = getUserIdByAuth(header);
        // if(!userService.isAdmin(userId)){
        //     throw new ResponseStatusException(HttpStatus.FORBIDDEN, "bạn không có quyền thực hiện lệnh này");
        // }
        return packageService.getAllPackage();
    }
    @GetMapping("/request/create")
    public ResponseEntity<List<ColumnPermissionResponseDTO>> drawFormCreate(@RequestHeader("Authorization") String header) {
        Long userId = getUserIdByAuth(header);
        if(!userService.isAdmin(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "bạn không có quyền thực hiện lệnh này");
        }
        System.out.println("PackageController: chuẩn bị tạo cây");
        List<ColumnPermissionResponseDTO> tree = permissionService.getPermissionTree("USER");
        return ResponseEntity.ok(tree);
    }
    @GetMapping("/request/edit/{id}")
    public ResponseEntity<PackageDetailResponseDto> drawFormEdit(
        @RequestHeader("Authorization") String header, 
        @PathVariable Long id
    ) {
        System.out.println("PackageController: chuẩn bị lấy thông tin của gói và trả ra cây quyền đã check");
        Long userId = getUserIdByAuth(header);
        if(!userService.isAdmin(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "bạn không có quyền thực hiện lệnh này");
        }
        PackageDetailResponseDto packageDetail = packageService.getDetailPackage(id);
        return ResponseEntity.ok(packageDetail);
    }
    @GetMapping("/{id}")
    public PackageDetailResponseDto getDetailPackage(@RequestHeader("Authorization") String header, @PathVariable Long id) {
        System.out.println("PackageController: chuẩn bị xem quyền");
        Long userId = getUserIdByAuth(header);
        if(!userService.isAdmin(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "bạn không có quyền thực hiện lệnh này");
        }
        return packageService.getDetailPackage(id);
    }
    @RequestMapping(value = "buy/{id}", method = {org.springframework.web.bind.annotation.RequestMethod.GET, org.springframework.web.bind.annotation.RequestMethod.POST})
    public ResponseEntity buyPackage (@PathVariable Long id, @RequestHeader ("Authorization") String header) {
        System.out.println("PackageController: chuẩn bị mua gói");
        // code chức năng mua gói ở đây
        Long userId = getUserIdByAuth(header);
        Long packageId = id;
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        System.out.println("PackageController: User: " + user.getFullName());
        Long companyId = user.getCompany().getId();
        packageService.buyPackage(companyId, packageId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("")
    @PreAuthorize ("hasAuthority('THEM_GOI')")
    public PackageResponseDTO createPack(@Valid @RequestBody PackageRequestDTO pDto, @RequestHeader("Authorization") String header) {
        // Long userId = getUserIdByAuth(header);
        // if(!userService.isAdmin(userId)){
        //     throw new ResponseStatusException(HttpStatus.FORBIDDEN, "bạn không có quyền thực hiện lệnh này");
        // }
        System.out.println("PackageController: chuẩn bị thêm quyền mới");
        return packageService.create(pDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize ("hasAuthority('SUA_GOI')")
    public PackageResponseDTO updatePack(
        @Valid @RequestBody PackageRequestDTO pDto, 
        @RequestHeader("Authorization") String header, 
        @PathVariable Long id
    ) {
        System.out.println("PackageController: chuẩn bị cập nhật gói");
        return packageService.update(pDto, id);
    }
    
    private Long getUserIdByAuth(String authHeader){
        String jwt = authHeader.substring(7);
        return jwtUtils.extractUserId(jwt);
    }
    @DeleteMapping ("/{id}")
    @PreAuthorize ("hasAuthority('XOA_GOI')")
    public ResponseEntity<Void> deletePack(@RequestHeader("Authorization") String header, @PathVariable Long id) {
        System.out.println("PackageController: chuẩn bị xóa gói");
        packageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
