package com.crm.BackendCrm.service;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.crm.BackendCrm.dto.Request.AdminRequestDTO;
import com.crm.BackendCrm.dto.Response.AdminResponseDTO;
import com.crm.BackendCrm.entity.Admin;
import com.crm.BackendCrm.repository.AdminRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public List<AdminResponseDTO> getAll(){
        return adminRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public AdminResponseDTO create(AdminRequestDTO aDto){
        Admin admin = new Admin();
        admin.setUserName(aDto.userName());
        admin.setPassWord(passwordEncoder.encode(aDto.passWord()));
        admin.setCreateAt(aDto.createAt());

        return toDTO(adminRepository.save(admin));
    }

    public AdminResponseDTO update(AdminRequestDTO aDto, Long id){
        Admin admin = adminRepository.findById(id).
        orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ko thấy tài khoản"));

        if(aDto.passWord() == null && !aDto.passWord().isBlank()){
            admin.setPassWord(passwordEncoder.encode(aDto.passWord()));
        }
        return toDTO(adminRepository.save(admin));
    }

    private AdminResponseDTO toDTO(Admin admin){
        return new AdminResponseDTO(
            admin.getId(),
            admin.getUserName(),
            admin.getPassWord(),
            admin.getCreateAt()
        );
    }
}
