package com.crm.BackendCrm.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crm.BackendCrm.dto.Response.CustomerTmpResponseDTO;
import com.crm.BackendCrm.repository.CustomerTmpRepository;
import com.crm.BackendCrm.entity.CustomerTmp;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomerTmpService {
    private final CustomerTmpRepository customerTmpRepository;

    public List<CustomerTmpResponseDTO> getAll() {
        return customerTmpRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    private CustomerTmpResponseDTO toDTO(CustomerTmp ct) {
        return new CustomerTmpResponseDTO(
                ct.getId(),
                ct.getImportId(),
                ct.getDateOfBirth(),
                ct.getCustomerName(),
                ct.getPhoneNumber(),
                ct.getLocation(),
                ct.getGender(),
                ct.getUserId(),
                ct.getCompanyId()
        );
    }
}
