package com.crm.BackendCrm.service;

import com.crm.BackendCrm.dto.Request.CustomerRequestDTO;
import com.crm.BackendCrm.dto.Response.CustomerResponseDTO;
import com.crm.BackendCrm.entity.Customer;
import com.crm.BackendCrm.entity.User;
import com.crm.BackendCrm.entity.Company;
import com.crm.BackendCrm.repository.CompanyRepository;
import com.crm.BackendCrm.repository.CustomerRepository;
import com.crm.BackendCrm.repository.CustomerTmpRepository;
import com.crm.BackendCrm.repository.UserRepository;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;

import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.crm.BackendCrm.entity.CustomerTmp;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CustomerTmpRepository customerTmpRepository;
    
    private final int itemPerPage = 5;

    public Page<CustomerResponseDTO> findAllInPage(int index){
        Pageable pageable = PageRequest.of(index, itemPerPage);
        Page<Customer> customers = customerRepository.findAll(pageable);
        return customers.map(this::toDTO);
    }

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

    public void saveFileData(InputStream file, String mappingJson) throws IOException {
        System.out.println("Bắt đầu quy trình Import bằng Bảng Tạm (Staging Table)...");
        Company defaultCompany = companyRepository.findById((long)1).
        orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        User defaultUser = userRepository.findById((long)1).
        orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Parse JSON Tọa độ (Index) từ Frontend gửi lên
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Integer> columnIndex = mapper.readValue(mappingJson, new TypeReference<Map<String, Integer>>(){});

        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0);

        // 1. Vòng lặp thứ nhất: Đi săn tất cả những SĐT/Email nào xuất hiện từ 2 lần trở lên
        Set<String> seenEmails = new HashSet<>();
        Set<String> duplicateEmails = new HashSet<>();
        
        Set<String> seenPhones = new HashSet<>();
        Set<String> duplicatePhones = new HashSet<>();

        DataFormatter formatter = new DataFormatter();

        sheet.forEach(row -> {
            if(row.getRowNum() == 0) return;
            try {
                if (columnIndex.containsKey("email") && columnIndex.get("email") != null) {
                    String email = formatter.formatCellValue(row.getCell(columnIndex.get("email")));
                    if (!email.isEmpty() && !seenEmails.add(email)) {
                        duplicateEmails.add(email); // Bắt được kẻ trùng lặp!
                    }
                }
                if (columnIndex.containsKey("phoneNumber") && columnIndex.get("phoneNumber") != null) {
                    String phone = formatter.formatCellValue(row.getCell(columnIndex.get("phoneNumber")));
                    if (!phone.isEmpty() && !seenPhones.add(phone)) {
                        duplicatePhones.add(phone); // Bắt được kẻ trùng lặp!
                    }
                }
            } catch (Exception ignored) {}
        });

        // 2. Vòng lặp thứ hai: Lọc dữ liệu, từ chối CẢ 2 thằng nếu nó nằm trong danh sách đen (duplicate)
        List<CustomerTmp> tmpList = new ArrayList<>();
        long importId = System.currentTimeMillis(); 

        sheet.forEach(row -> {
            if(row.getRowNum() == 0) return;
            
            try {
                String email = "";
                if (columnIndex.containsKey("email") && columnIndex.get("email") != null) {
                    email = formatter.formatCellValue(row.getCell(columnIndex.get("email")));
                }
                
                String phone = "";
                if (columnIndex.containsKey("phoneNumber") && columnIndex.get("phoneNumber") != null) {
                    phone = formatter.formatCellValue(row.getCell(columnIndex.get("phoneNumber")));
                }

                // KIỂM TRA QUYẾT ĐỊNH: Có nằm trong danh sách đen không?
                if (!email.isEmpty() && duplicateEmails.contains(email)) {
                    System.out.println("Dòng " + row.getRowNum() + " bị loại vì Email này bị phát hiện có clone trong file: " + email);
                    return; // Vứt! Không lấy thằng nào hết
                }
                if (!phone.isEmpty() && duplicatePhones.contains(phone)) {
                    System.out.println("Dòng " + row.getRowNum() + " bị loại vì SĐT này bị phát hiện có clone trong file: " + phone);
                    return; // Vứt! Không lấy thằng nào hết
                }

                // Nếu không trùng, tạo Entity Tmp
                CustomerTmp tmp = new CustomerTmp();
                tmp.setImportId(importId);
                tmp.setEmail(email);
                tmp.setPhoneNumber(phone);

                if (columnIndex.containsKey("name") && columnIndex.get("name") != null) {
                    tmp.setCustomerName(formatter.formatCellValue(row.getCell(columnIndex.get("name"))));
                }
                if (columnIndex.containsKey("dateOfBirth") && columnIndex.get("dateOfBirth") != null) {
                    tmp.setDateOfBirth(formatter.formatCellValue(row.getCell(columnIndex.get("dateOfBirth"))));
                }
                if (columnIndex.containsKey("location") && columnIndex.get("location") != null) {
                    tmp.setLocation(formatter.formatCellValue(row.getCell(columnIndex.get("location"))));
                }
                
                tmp.setGender(true); // Mặc định
                if (columnIndex.containsKey("gender") && columnIndex.get("gender") != null) {
                    Cell genderCell = row.getCell(columnIndex.get("gender"));
                    if (genderCell != null) {
                        try {
                            tmp.setGender(genderCell.getBooleanCellValue());
                        } catch (Exception e) {
                            String genderStr = formatter.formatCellValue(genderCell).toLowerCase();
                            tmp.setGender(genderStr.equals("true") || genderStr.equals("1"));
                        }
                    }
                }
                
                long compId = defaultCompany.getId();
                if (columnIndex.containsKey("companyId") && columnIndex.get("companyId") != null) {
                    try {
                        compId = Long.parseLong(formatter.formatCellValue(row.getCell(columnIndex.get("companyId"))));
                    } catch (Exception ignored) {}
                }
                tmp.setCompanyId(compId);

                long usrId = defaultUser.getId();
                if (columnIndex.containsKey("userId") && columnIndex.get("userId") != null) {
                    try {
                        usrId = Long.parseLong(formatter.formatCellValue(row.getCell(columnIndex.get("userId"))));
                    } catch (Exception ignored) {}
                }
                tmp.setUserId(usrId);

                tmpList.add(tmp);
            } catch (Exception e) {
                System.out.println("Lỗi parse dòng " + row.getRowNum() + ": " + e.getMessage());
            }
        });

        // System.out.println("Đang lưu " + tmpList.size() + " dòng vào bảng Tmp...");
        // customerTmpRepository.saveAll(tmpList);

        String jsonData = mapper.writeValueAsString(tmpList);

        System.out.println("Đang gọi Stored Procedure xử lý data nội bộ DB...");
        customerTmpRepository.processCustomerImport(jsonData);

        System.out.println("Import THÀNH CÔNG ! (Bằng sức mạnh của Stored Procedure)");
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
