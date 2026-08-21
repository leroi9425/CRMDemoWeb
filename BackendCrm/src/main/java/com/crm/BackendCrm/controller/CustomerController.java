package com.crm.BackendCrm.controller;

import com.crm.BackendCrm.dto.CustomerRequestDTO;
import com.crm.BackendCrm.dto.CustomerResponseDTO;
import com.crm.BackendCrm.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAuthority('XEM_KHACH_HANG')")
    public List<CustomerResponseDTO> getAll() {
        return customerService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('XEM_KHACH_HANG')")
    public CustomerResponseDTO getById(@PathVariable Long id) {
        return customerService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('THEM_KHACH_HANG')")
    public ResponseEntity<CustomerResponseDTO> create(@Valid @RequestBody CustomerRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(dto));
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
}
