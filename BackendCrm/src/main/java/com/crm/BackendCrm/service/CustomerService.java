package com.crm.BackendCrm.service;

import com.crm.BackendCrm.dto.CustomerRequestDTO;
import com.crm.BackendCrm.dto.CustomerResponseDTO;
import com.crm.BackendCrm.entity.Customer;
import com.crm.BackendCrm.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public List<CustomerResponseDTO> getAll() {
        return customerRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CustomerResponseDTO getById(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        return toDTO(customer);
    }

    public CustomerResponseDTO create(CustomerRequestDTO dto) {
        if (customerRepository.existsByEmail(dto.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (customerRepository.existsByPhoneNumber(dto.phoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number already exists");
        }

        Customer customer = new Customer();
        customer.setCustomerName(dto.name());
        customer.setEmail(dto.email());
        customer.setPhoneNumber(dto.phoneNumber());
        customer.setDateOfBirth(dto.dateOfBirth());
        customer.setLocation(dto.location());
        customer.setGender(dto.gender());
        return toDTO(customerRepository.save(customer));
    }

    public CustomerResponseDTO update(Long id, CustomerRequestDTO dto) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        
        if (!customer.getEmail().equals(dto.email()) && customerRepository.existsByEmail(dto.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (!customer.getPhoneNumber().equals(dto.phoneNumber()) && customerRepository.existsByPhoneNumber(dto.phoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number already exists");
        }

        customer.setCustomerName(dto.name());
        customer.setEmail(dto.email());
        customer.setPhoneNumber(dto.phoneNumber());
        customer.setDateOfBirth(dto.dateOfBirth());
        customer.setLocation(dto.location());
        customer.setGender(dto.gender());
        return toDTO(customerRepository.save(customer));
    }

    public void delete(Long id) {
        customerRepository.deleteById(id);
    }

    private CustomerResponseDTO toDTO(Customer c) {
        return new CustomerResponseDTO(
            c.getId(), c.getCustomerName(), c.getPhoneNumber(), c.getEmail(), 
            c.getDateOfBirth(), c.getLocation(), c.isGender(), c.getCreatedAt()
        );
    }
}
