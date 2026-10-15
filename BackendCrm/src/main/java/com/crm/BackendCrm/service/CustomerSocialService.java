package com.crm.BackendCrm.service;

import com.crm.BackendCrm.repository.CustomerRepository;
import com.crm.BackendCrm.repository.EmailRepository;

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
    private final EmailRepository emailRepository;
    private  final CompanyRepository companyRepository;

    public boolean isEmailExist(String emailAddress, long userId){
        Email email = emailRepository.findByEmailAddress(emailAddress)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy email: "+emailAddress));

        String customerCode = email.getCustomerCode();
        Customer customer = customerRepository.findByCustomerCode(customerCode)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng có code:"+customerCode));

        long cuserId = customer.getUser().getId();
        if(cuserId == userId)
            return true;
        else
            return false;
    }
    public boolean isPhoneExist(String phoneNumber){
        return true;
    }
    
    public ResponseEntity<?> reviceCustomer(CustomerSocialRequestDTO data){
        try {
            String customerCode = customerService.createCustomerCode();
            System.out.println(customerCode);
            System.out.println("end System.out.println(customerCode);");
            System.out.println("System.out.println(data);");
            System.out.println(data);
            System.out.println("end System.out.println(data);");
            // check trùng email, sdt
            // if(emailRepository.findByEmailAddress(data.email()) != null && customerRepository.existsByPhoneNumberAndUserId(data.phoneNumber(), data.userId())){
            //     System.out.println("Trùng email và số điện thoại");
            //     return ResponseEntity.notFound().build();
            // }

            User user = userRepository.findById(data.userId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ko tim thay user id:" + data.userId()));
            Company company = companyRepository.findById(data.companyId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ko tim thay cong ty id:" + data.companyId()));

            System.out.println("Tạo customer để thêm");
            Customer customer = new Customer();
            customer.setCustomerName(data.name());
            customer.setPhoneNumber(data.phoneNumber());
            customer.setContact(null);
            customer.setCreatedAt(LocalDateTime.now());
            customer.setDateOfBirth("1-1-2026");
            customer.setGender(true);
            System.out.println("emails khi them (customerSocialService)" + data.email());
            customer.setEmail(data.email());
            customer.setLocation("Việt Nam");
            customer.setUser(user);
            customer.setCustomerCode(customerCode);
            customer.setCompany(company);
            
            customerRepository.save(customer);

            if(data.email() != null){
                Email email = new Email();
                email.setCustomerCode(customerCode);
                email.setEmailAddress(data.email());
                emailRepository.save(email);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Loi khi nhan khach tu webhook: ");
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
        
    }
}
