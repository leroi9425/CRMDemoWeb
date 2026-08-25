package com.crm.BackendCrm.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import com.crm.BackendCrm.service.ProductService;

import jakarta.validation.Valid;

import com.crm.BackendCrm.dto.ProductResponseDTO;
import com.crm.BackendCrm.dto.ProductRequestDTO;

import lombok.RequiredArgsConstructor;
import java.util.List;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasAuthority('XEM_SAN_PHAM')")
    public List<ProductResponseDTO> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('XEM_SAN_PHAM')")
    public ProductResponseDTO getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('THEM_SAN_PHAM')")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(dto));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SUA_SAN_PHAM')")
    public ProductResponseDTO updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO dto) {
        return productService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('XOA_SAN_PHAM')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
