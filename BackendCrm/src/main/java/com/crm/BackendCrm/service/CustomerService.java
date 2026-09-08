package com.crm.BackendCrm.service;

import com.crm.BackendCrm.dto.Request.CustomerRequestDTO;
import com.crm.BackendCrm.dto.Response.ContactPositionResponseDTO;
import com.crm.BackendCrm.dto.Response.ContactResponseDTO;
import com.crm.BackendCrm.dto.Response.CustomerDetailResponseDTO;
import com.crm.BackendCrm.dto.Response.CustomerResponseDTO;
import com.crm.BackendCrm.dto.Response.EmailResponseDTO;
import com.crm.BackendCrm.entity.Customer;
import com.crm.BackendCrm.entity.User;
import com.crm.BackendCrm.entity.Company;
import com.crm.BackendCrm.entity.Contact;
import com.crm.BackendCrm.entity.Email;
import com.crm.BackendCrm.entity.Position;
import com.crm.BackendCrm.repository.CompanyRepository;
import com.crm.BackendCrm.repository.ContactRepository;
import com.crm.BackendCrm.repository.CustomerRepository;
import com.crm.BackendCrm.repository.CustomerTmpRepository;
import com.crm.BackendCrm.repository.EmailRepository;
import com.crm.BackendCrm.repository.PositionRepository;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final EmailRepository emailRepository;
    private final ContactRepository contactRepository;
    private final PositionRepository positionRepository;
    // private final NotificationService notificationService;
    
    private final int itemPerPage = 5;

    public Page<CustomerResponseDTO> findAllInPage(int index, Long userId){
        Pageable pageable = PageRequest.of(index, itemPerPage);
        Page<Customer> customers = customerRepository.findByUserId(userId, pageable);
        return customers.map(this::toDTO);
    }

    public List<CustomerResponseDTO> getAll() { 
        return customerRepository.
        findAll().
        stream().
        map(this::toDTO).
        collect(Collectors.toList());
    }

    private EmailResponseDTO emailToDTO(Email email){
        return new EmailResponseDTO(
            email.getId(),
            email.getEmailAddress(),
            email.getCustomerCode()
        );
    }
    private ContactResponseDTO contactToDTO(Contact contact){
        return new ContactResponseDTO(
            contact.getId(),
            contact.getContactName(),
            contact.getPhoneNumber(),
            contact.getEmail(),
            contact.getCustomerCode(),
            contact.getPosition().getId()
        );
    }

    public CustomerDetailResponseDTO getCustomerDetail(Long id){
        Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "khong tim thay khach hang"));

        List<EmailResponseDTO> emails = emailRepository.getAllByCustomerCode(customer.getCustomerCode())
                                        .stream().map(this::emailToDTO)
                                        .collect(Collectors.toList());

        List<ContactResponseDTO> contacts = contactRepository.getByCustomerCode(customer.getCustomerCode())
                                            .stream().map(this::contactToDTO)
                                            .collect(Collectors.toList());
        List<ContactPositionResponseDTO> contactPosition = new ArrayList<>();
        for(int i=0 ; i<contacts.size() ; i++){
            System.out.print("index contact: "+i);
            Long pid = contacts.get(i).positionId();

            Position position = positionRepository.findById(pid)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ko tim thay póition"));

            ContactPositionResponseDTO cp = new ContactPositionResponseDTO(
                contacts.get(i).id(),
                contacts.get(i).contactName(),
                contacts.get(i).phoneNumber(),
                contacts.get(i).email(),
                contacts.get(i).customerCode(),
                position.getNamePosition()
            );

            contactPosition.add(cp);
        }

        CustomerDetailResponseDTO cusDetail = new CustomerDetailResponseDTO(
            customer.getCustomerName(),   // String
            customer.getDateOfBirth(),  // String
            emails,                      // List<Email>
            customer.getPhoneNumber(),   // String
            customer.isGender(),
            contactPosition                    // List<ContactPosition>
        );
        return cusDetail;
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
                    String rawEmail = formatter.formatCellValue(row.getCell(columnIndex.get("email")));

                    String[] emailList = rawEmail.split(";"); //tạo mảng email
                    for (String email : emailList) {
                        email = email.trim();
                        if (!email.isEmpty() && !seenEmails.add(email)) {
                            duplicateEmails.add(email); // Bắt được kẻ trùng lặp!
                        }
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
        List<CustomerTmp> tmpCustomerList = new ArrayList<>();
        List<Email> tmpEmailList = new ArrayList<>();

        sheet.forEach(row -> {
            if(row.getRowNum() == 0) return;
            try {
                // 1. TẠO MỎ NEO TRƯỚC ĐỂ DÙNG CHUNG
                String customerCode = createCustomerCode(row.getRowNum());
                
                String rawEmail = "";
                if (columnIndex.containsKey("email") && columnIndex.get("email") != null) {
                    rawEmail = formatter.formatCellValue(row.getCell(columnIndex.get("email")));
                }
                
                String phone = "";
                if (columnIndex.containsKey("phoneNumber") && columnIndex.get("phoneNumber") != null) {
                    phone = formatter.formatCellValue(row.getCell(columnIndex.get("phoneNumber")));
                }

                // 2. CHECK DANH SÁCH ĐEN & LƯU TẠM EMAIL
                // Dùng local list để lưu tạm các email của dòng này, lỡ bị loại thì sẽ vứt hết
                List<Email> localEmails = new ArrayList<>();
                String[] emailList = rawEmail.split(";");
                for (String email : emailList) {
                    email = email.trim();
                    if (!email.isEmpty()) {
                        if (duplicateEmails.contains(email)) {
                            System.out.println("Dòng " + row.getRowNum() + " bị loại vì Email clone: " + email);
                            return; // Trùng 1 cái là vứt cả dòng!
                        }
                        // Tạo object Email riêng lẻ, nhét luôn mã mỏ neo
                        Email emailtmp = new Email();
                        emailtmp.setEmailAddress(email);
                        emailtmp.setCustomerCode(customerCode);
                        localEmails.add(emailtmp);
                    }
                }
                
                if (!phone.isEmpty() && duplicatePhones.contains(phone)) {
                    System.out.println("Dòng " + row.getRowNum() + " bị loại vì SĐT này bị clone: " + phone);
                    return; // Vứt
                }

                // 3. VƯỢT QUA BÀI TEST -> TẠO ENTITY TMP
                CustomerTmp custmp = new CustomerTmp();
                custmp.setCustomerCode(customerCode);
                custmp.setPhoneNumber(phone);

                if (columnIndex.containsKey("name") && columnIndex.get("name") != null) {
                    custmp.setCustomerName(formatter.formatCellValue(row.getCell(columnIndex.get("name"))));
                }
                if (columnIndex.containsKey("dateOfBirth") && columnIndex.get("dateOfBirth") != null) {
                    custmp.setDateOfBirth(formatter.formatCellValue(row.getCell(columnIndex.get("dateOfBirth"))));
                }
                if (columnIndex.containsKey("location") && columnIndex.get("location") != null) {
                    custmp.setLocation(formatter.formatCellValue(row.getCell(columnIndex.get("location"))));
                }

                custmp.setGender(true); // Mặc định
                if (columnIndex.containsKey("gender") && columnIndex.get("gender") != null) {
                    Cell genderCell = row.getCell(columnIndex.get("gender"));
                    if (genderCell != null) {
                        try {
                            custmp.setGender(genderCell.getBooleanCellValue());
                        } catch (Exception e) {
                            String genderStr = formatter.formatCellValue(genderCell).toLowerCase();
                            custmp.setGender(genderStr.equals("true") || genderStr.equals("1"));
                        }
                    }
                }

                custmp.setCompanyId(defaultCompany.getId()); // Mặc định
                custmp.setUserId((long)2); // Mặc định

                // 4. LƯU VÀO LIST TỔNG
                tmpCustomerList.add(custmp);
                tmpEmailList.addAll(localEmails); // Thêm tất cả email của khách này vào list tổng
                
            } catch (Exception e) {
                System.out.println("Lỗi parse dòng " + row.getRowNum() + ": " + e.getMessage());
            }
        });

        // System.out.println("Đang lưu " + tmpList.size() + " dòng vào bảng Tmp...");
        // customerTmpRepository.saveAll(tmpList);

        String jsonCustomer = mapper.writeValueAsString(tmpCustomerList);
        String jsonEmail = mapper.writeValueAsString(tmpEmailList);

        System.out.println("Đang gọi Stored Procedure xử lý data nội bộ DB...");
        customerTmpRepository.processCustomerImport(jsonCustomer, jsonEmail);

        System.out.println("Import THÀNH CÔNG ! (Bằng sức mạnh của Stored Procedure)");
    }

    public CustomerResponseDTO create(CustomerRequestDTO dto, Long senderId) {
        // User sender = userRepository.findById(senderId)
        //     .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found"));

        // if (customerRepository.existsByEmail(dto.email())) {
        //     throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        // }
        if (customerRepository.existsByPhoneNumber(dto.phoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number already exists");
        }

        Company company = companyRepository.findById(dto.companyId()).
        orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        User user = userRepository.findById(dto.userId()).
        orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String customerCode = createCustomerCode();

        Customer customer = new Customer();
        customer.setCustomerName(dto.name());
        // customer.setEmail(dto.email());
        customer.setPhoneNumber(dto.phoneNumber());
        customer.setDateOfBirth(dto.dateOfBirth());
        customer.setLocation(dto.location());
        customer.setGender(dto.gender());
        customer.setCompany(company);
        customer.setUser(user);
        customer.setCustomerCode(customerCode);


        // bắt đầu gửi thông báo
        // User recipient = userRepository.findById(dto.userId())
        //     .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        // notificationService.createAndSendNotification(
        //     sender,
        //     recipient,
        //     "Bạn có khách hàng mới được thêm vào: " + dto.name()
        // );
        return toDTO(customerRepository.save(customer));
    }

    public CustomerResponseDTO update(Long id, CustomerRequestDTO dto) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        
        // if (!customer.getEmail().equals(dto.email()) && customerRepository.existsByEmail(dto.email())) {
        //     throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        // }
        if (!customer.getPhoneNumber().equals(dto.phoneNumber()) && customerRepository.existsByPhoneNumber(dto.phoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number already exists");
        }

        customer.setCustomerName(dto.name());
        // customer.setEmail(dto.email());
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
            c.getId(), c.getCustomerName(), c.getPhoneNumber(),c.getDateOfBirth(), c.getLocation(), c.isGender(), c.getCreatedAt(), c.getCompany().getId(),c.getUser().getId(), c.getCustomerCode()
        );
    }

    public String createCustomerCode() {
        return "KH" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    public String createCustomerCode(int excelRowIndex) {
        return "KH" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + excelRowIndex;
    }
}
