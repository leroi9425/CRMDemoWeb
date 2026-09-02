package com.crm.BackendCrm.service;

import com.crm.BackendCrm.dto.Request.CustomerRequestDTO;
import com.crm.BackendCrm.dto.Response.CustomerResponseDTO;
import com.crm.BackendCrm.entity.Customer;
import com.crm.BackendCrm.entity.User;
import com.crm.BackendCrm.entity.Company;
import com.crm.BackendCrm.repository.CompanyRepository;
import com.crm.BackendCrm.repository.CustomerRepository;
import com.crm.BackendCrm.repository.UserRepository;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;

import lombok.RequiredArgsConstructor;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public List<CustomerResponseDTO> getAll() { 
        return customerRepository.
        findAll().
        stream().
        map(this::toDTO).
        collect(Collectors.toList());
    }

    public CustomerResponseDTO getById(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        return toDTO(customer);
    }

    public void saveFileData(InputStream file) throws IOException{
        Company company = companyRepository.findById((long)1).
        orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        User user = userRepository.findById((long)1).
        orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0);

        Row headerRow = sheet.getRow(0);

        Map<String, Integer> columnIndex = new HashMap<>();
        for(Cell cell : headerRow){
            String headerName = cell.getStringCellValue().trim();
            columnIndex.put(headerName, cell.getColumnIndex()); //ta chuyển đổi tên cột trong file excel sang số index
            System.out.print("headerName: "+headerName+" index: "+cell.getColumnIndex());
        }

        System.out.print("column index: "+columnIndex);
        sheet.forEach(row -> {
            Customer customer = new Customer();

            DataFormatter formatter = new DataFormatter();
            // ...
            if(row.getRowNum() != 0) {
                // Dùng formatter.formatCellValue() để bọc lại, nó sẽ tự động lấy mọi thứ biến thành String!
                customer.setCustomerName(formatter.formatCellValue(row.getCell(columnIndex.get("customerName"))));
                customer.setPhoneNumber(formatter.formatCellValue(row.getCell(columnIndex.get("phoneNumber"))));
                customer.setEmail(formatter.formatCellValue(row.getCell(columnIndex.get("email"))));
                customer.setDateOfBirth(formatter.formatCellValue(row.getCell(columnIndex.get("dateOfBirth"))));
                customer.setLocation(formatter.formatCellValue(row.getCell(columnIndex.get("location"))));
                
                // Riêng giới tính là Boolean thì vẫn giữ nguyên (Miễn là trong Excel ghi TRUE/FALSE)
                customer.setGender(row.getCell(columnIndex.get("gender")).getBooleanCellValue());
                
                customer.setCompany(company);
                customer.setUser(user);

                System.out.print("customer: "+customer);
                toDTO(customerRepository.save(customer));
            }
        });
    }

    public CustomerResponseDTO create(CustomerRequestDTO dto) {
        if (customerRepository.existsByEmail(dto.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (customerRepository.existsByPhoneNumber(dto.phoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number already exists");
        }

        Company company = companyRepository.findById(dto.companyId()).
        orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        User user = userRepository.findById(dto.userId()).
        orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        Customer customer = new Customer();
        customer.setCustomerName(dto.name());
        customer.setEmail(dto.email());
        customer.setPhoneNumber(dto.phoneNumber());
        customer.setDateOfBirth(dto.dateOfBirth());
        customer.setLocation(dto.location());
        customer.setGender(dto.gender());
        customer.setCompany(company);
        customer.setUser(user);
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
            c.getDateOfBirth(), c.getLocation(), c.isGender(), c.getCreatedAt(), c.getCompany().getId(),c.getUser().getId()
        );
    }
}
