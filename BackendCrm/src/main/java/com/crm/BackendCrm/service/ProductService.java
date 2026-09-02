package com.crm.BackendCrm.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.crm.BackendCrm.repository.ProductRepository;
import com.crm.BackendCrm.dto.Request.ProductRequestDTO;
import com.crm.BackendCrm.dto.Response.ProductResponseDTO;
import com.crm.BackendCrm.entity.Product;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductResponseDTO> getAll() {
       return productRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ProductResponseDTO getById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return toDTO(product);
    }

    public ProductResponseDTO create(ProductRequestDTO dto){
        Product product = new Product();
        product.setProductName(dto.productName());
        product.setDetail(dto.detail());
        return toDTO(productRepository.save(product));
    }

    public ProductResponseDTO update(long id, ProductRequestDTO dto) {
        Product product = productRepository.findById(id).
        orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        product.setProductName(dto.productName());
        product.setDetail(dto.detail());
        return toDTO(productRepository.save(product));        
    }

    public void delete(long id) {
        productRepository.deleteById(id);
    }

    private ProductResponseDTO toDTO(Product product) {
        return new ProductResponseDTO(product.getId(), product.getProductName(), product.getDetail());
    }
}
