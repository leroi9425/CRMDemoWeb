package com.crm.BackendCrm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import com.crm.BackendCrm.dto.Request.CompanyRequestDTO;
import com.crm.BackendCrm.dto.Response.CompanyResponseDTO;
import com.crm.BackendCrm.dto.Response.UserResponseDTO;
import com.crm.BackendCrm.service.CompanyService;
import org.springframework.data.domain.Page;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("api/companies")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping
    @PreAuthorize("hasAuthority('XEM_CONG_TY')")
    public List<CompanyResponseDTO> getAll() {
        return companyService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('XEM_CONG_TY')")
    public CompanyResponseDTO getById(@PathVariable Long id) {
        return companyService.findById(id);
    }

    @GetMapping("/{id}/detail")
    @PreAuthorize("hasAuthority('XEM_CONG_TY')")
    public List<UserResponseDTO> getMethodName(@PathVariable Long id) {
        return companyService.getAllUser(id);
    }
    

    @PostMapping
    @PreAuthorize("hasAuthority('THEM_CONG_TY')")
    public CompanyResponseDTO create (@Valid @RequestBody CompanyRequestDTO cDto ) {
        return companyService.create(cDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SUA_CONG_TY')")
    public CompanyResponseDTO update(@PathVariable Long id, @Valid @RequestBody CompanyRequestDTO cDTO) {
        return companyService.update(cDTO, id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('XOA_CONG_TY')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/page={index}")
    public Page<CompanyResponseDTO> getMethodName(@PathVariable int index) {
        return companyService.findAllInPage(index);
    }
}
