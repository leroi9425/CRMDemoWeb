package com.crm.BackendCrm.service;

import com.crm.BackendCrm.repository.CustomerRepository;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.crm.BackendCrm.dto.Request.CustomerSocialRequestDTO;
import com.crm.BackendCrm.entity.Customer;
import com.crm.BackendCrm.entity.Email;
import com.crm.BackendCrm.entity.User;
import com.crm.BackendCrm.entity.Company;
import com.crm.BackendCrm.repository.CompanyRepository;
import com.crm.BackendCrm.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CustomerSocialService {
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;
    private final UserRepository userRepository;

    private  final CompanyRepository companyRepository;
    
    public ResponseEntity<?> reviceCustomer(CustomerSocialRequestDTO data){
        String customerCode = customerService.createCustomerCode();
        User user = userRepository.findById((long)2)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ko tim thay user"));
        Company company = companyRepository.findById((long)1)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));


        Customer customer = new Customer();
        customer.setCustomerName(data.name());
        customer.setPhoneNumber(data.phoneNumber());
        customer.setContact(null);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setDateOfBirth("1-1-2026");
        customer.setGender(true);
        customer.setEmail(data.email());
        customer.setLocation("Việt Nam");
        customer.setUser(user);
        customer.setCustomerCode(customerCode);
        customer.setCompany(company);

        Email email = new Email();
        email.setCustomerCode(customerCode);
        email.setEmailAddress(data.email());

        customerRepository.save(customer);
        return ResponseEntity.ok().build();
    }
}
