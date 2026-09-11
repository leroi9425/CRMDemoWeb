package com.crm.BackendCrm.controller;

import com.crm.BackendCrm.dto.Request.CustomerFilterRequestDTO;
import com.crm.BackendCrm.dto.Request.CustomerRequestDTO;
import com.crm.BackendCrm.dto.Response.CustomerDetailResponseDTO;
import com.crm.BackendCrm.dto.Response.CustomerResponseDTO;
import com.crm.BackendCrm.entity.Customer;
import com.crm.BackendCrm.security.JwtUtils;
import com.crm.BackendCrm.service.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;


@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {
    private final CustomerService customerService;
    private final JwtUtils jwtUtils;

    // @GetMapping
    // @PreAuthorize("hasAuthority('XEM_KHACH_HANG')")
    // public List<CustomerResponseDTO> getAll() {
    //     return customerService.getAll();
    // }
    @PostMapping("/export")
    public ResponseEntity<byte[]> exportFile(
        @RequestBody CustomerFilterRequestDTO cfDto, 
        @RequestHeader("Authorization") String authHeader,
        @RequestParam List<String> fields
    ) throws IOException{
        String jwt = authHeader.substring(7);
        Long userId = jwtUtils.extractUserId(jwt);
        
        List<Customer> customers = customerService.findAllFilter(cfDto, userId);
        byte[] excel = customerService.createExcel(customers, fields);

        return ResponseEntity.ok().header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=customers.xlsx"
            ) .contentType(
                MediaType.APPLICATION_OCTET_STREAM
            )
            .body(excel);
    }

    @PostMapping("/filter/page={index}")
    public Page<CustomerResponseDTO> getPageFilter (
        @Valid @RequestBody CustomerFilterRequestDTO cfrDTO,
        @PathVariable int index, 
        @RequestHeader("Authorization") String authHeader
    ) {
        String jwt = authHeader.substring(7);
        Long userId = jwtUtils.extractUserId(jwt);

        return customerService.findAllPageFIlter(cfrDTO, index, userId);
    }
    

    @GetMapping("/page={index}")
    @PreAuthorize("hasAuthority('XEM_KHACH_HANG')")
    public Page<CustomerResponseDTO> getPage(@PathVariable int index, @RequestHeader("Authorization") String authHeader) {
        System.out.print("trang hien tai la: " + index);
        String jwt = authHeader.substring(7);
        Long userId = jwtUtils.extractUserId(jwt);

        return customerService.findAllInPage(index, userId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('XEM_KHACH_HANG')")
    public CustomerResponseDTO getById(@PathVariable Long id) {
        return customerService.getById(id);
    }

    @GetMapping("/detail/{id}")
    public CustomerDetailResponseDTO getCustomerDetail(@PathVariable Long id) {
        return customerService.getCustomerDetail(id);
    }
    

    @PostMapping
    @PreAuthorize("hasAuthority('THEM_KHACH_HANG')")
    public ResponseEntity<CustomerResponseDTO> create(@Valid @RequestBody CustomerRequestDTO dto, @RequestHeader("Authorization") String authHeader) {
        String jwt = authHeader.substring(7);
        Long userId = jwtUtils.extractUserId(jwt);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(dto, userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SUA_KHACH_HANG')")
    public CustomerResponseDTO update(@PathVariable Long id, @Valid @RequestBody CustomerRequestDTO dto) {
        return customerService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('XOA_KHACH_HANG')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ResponseEntity<String> saveFileData (
        @RequestParam("file") MultipartFile file,
        @RequestParam("mapping") String mappingJson
    ) throws IOException {
        customerService.saveFileData(file.getInputStream(), mappingJson);
        return ResponseEntity.ok("file da luu vao database");
    }
}
