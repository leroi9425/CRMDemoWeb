package com.crm.BackendCrm.service;

import com.crm.BackendCrm.dto.Request.CustomerFilterRequestDTO;
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
import com.crm.BackendCrm.entity.MapExcel;
import com.crm.BackendCrm.entity.Position;
import com.crm.BackendCrm.repository.CompanyRepository;
import com.crm.BackendCrm.repository.ContactRepository;
import com.crm.BackendCrm.repository.CustomerRepository;
import com.crm.BackendCrm.repository.CustomerTmpRepository;
import com.crm.BackendCrm.repository.EmailRepository;
import com.crm.BackendCrm.repository.MapExcelRepository;
import com.crm.BackendCrm.repository.PositionRepository;
import com.crm.BackendCrm.repository.UserRepository;
import com.crm.BackendCrm.specification.CustomerSpecification;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
// import org.springframework.boot.data.autoconfigure.web.DataWebProperties.Sort;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.ByteArrayOutputStream;
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
    private final MapExcelRepository mapExcelRepository;
    
    private final NotificationService notificationService;

    // private final NotificationService notificationService;
    
    private final int itemPerPage = 5;

    public Page<CustomerResponseDTO> findAllInPage(int index, Long userId){
        Pageable pageable = PageRequest.of(index, itemPerPage);
        Page<Customer> customers = customerRepository.findByUserId(userId, pageable);
        return customers.map(this::toDTO);
    }
    public List<Customer> findAllFilter(CustomerFilterRequestDTO cfDto, Long userId){
        Specification<Customer> spec = filterByUserId(cfDto, userId);
        List<Customer> customers = customerRepository.findAll(spec);     // đây là danh sách đã filter có cả email   
        
        return customers;
    }

    public byte[] createExcel(List<Customer> customers, List<String> fields) throws IOException{
        List<MapExcel> mapExcels = mapExcelRepository.findAll();
        Map<String, String> headerMap = mapExcels.stream().collect(Collectors.toMap(
                                            MapExcel::getPropertieName,
                                            MapExcel::getColumnName
                                        ));
        
        List<Contact> contacts = contactRepository.findAll();
        Map<String, List<Contact>> contactMap = contacts.stream()
                                                .filter(contact -> contact.getCustomerCode() != null)
                                                .collect(Collectors.groupingBy(Contact::getCustomerCode));

        SXSSFWorkbook workbook = new SXSSFWorkbook(100);

        Sheet sheet = workbook.createSheet("Customers");

        // Header
        Row header = sheet.createRow(0);
        for(int i=0 ; i<fields.size() ; i++){
            String columnName = fields.get(i);
            String headerTitle = headerMap.get(columnName);
            if (headerTitle == null) {
                // Fallback nếu trong DB map_excel sếp chưa thêm dòng này
                if (columnName.equals("gender")) headerTitle = "Giới tính";
                else headerTitle = columnName; 
            }
            header.createCell(i).setCellValue(headerTitle);
        }

        int rowIndex = 1;

        for (Customer customer : customers) {

            Row row = sheet.createRow(rowIndex++);

            for(int i = 0; i < fields.size(); i++){

                if(fields.get(i).equals("name")) row.createCell(i).setCellValue(customer.getCustomerName());
                else if(fields.get(i).equals("email")) row.createCell(i).setCellValue(customer.getEmail());
                else if(fields.get(i).equals("phoneNumber")) row.createCell(i).setCellValue(customer.getPhoneNumber());
                else if(fields.get(i).equals("location")) row.createCell(i).setCellValue(customer.getLocation());
                else if(fields.get(i).equals("dateOfBirth")) row.createCell(i).setCellValue(customer.getDateOfBirth());
                else if(fields.get(i).equals("customerCode")) row.createCell(i).setCellValue(customer.getCustomerCode());
                else if(fields.get(i).equals("company")) row.createCell(i).setCellValue(customer.getCompany() != null ? customer.getCompany().getName() : "");
                else if(fields.get(i).equals("gender")) row.createCell(i).setCellValue(customer.isGender() ? "Nam" : "Nữ");
                else if(fields.get(i).equals("contacts")){
                    List<Contact> cusContacts = contactMap.getOrDefault(customer.getCustomerCode(), new ArrayList<>());
                    String contactRow = "";
                    for(Contact c : cusContacts){
                        contactRow +=  c.getPosition().getNamePosition() + "-" +c.getContactName() +" SĐT: " + c.getPhoneNumber() + " Email: " + c.getEmail() + "\n";
                    }
                    row.createCell(i).setCellValue(contactRow);
                }
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // tạo 1 vùng ram để chứa các byte của file

        workbook.write(outputStream);          //  ghi vào vùng nhớ đó
        workbook.dispose();      // xóa các file tạm mà row từ ram đi vào file tạm
        workbook.close();

        return outputStream.toByteArray();
    }

    public Page<CustomerResponseDTO> findAllPageFIlter(CustomerFilterRequestDTO cfDto, int index, Long userId){
        Specification<Customer> spec = filterByUserId(cfDto, userId);        
        Sort sort = createSort(cfDto);

        Pageable page = PageRequest.of(index, itemPerPage, sort);
        Page<Customer> customersPage = customerRepository.findAll(spec,page);

        return customersPage.map(this::toDTO);
    }

    private Sort createSort(CustomerFilterRequestDTO cfDto){
        if(cfDto.sortField() == null || cfDto.sortField().isBlank()){
            return Sort.unsorted();
        }

        if("DESC".equalsIgnoreCase(cfDto.sortDirection())){
            return Sort.by(cfDto.sortField()).descending();
        }

        return Sort.by(cfDto.sortField()).ascending();
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

        String jsonCustomer = mapper.writeValueAsString(tmpCustomerList);
        String jsonEmail = mapper.writeValueAsString(tmpEmailList);

        System.out.println("Đang gọi Stored Procedure xử lý data nội bộ DB...");
        customerTmpRepository.processCustomerImport(jsonCustomer, jsonEmail);

        System.out.println("Import THÀNH CÔNG ! (Bằng sức mạnh của Stored Procedure)");
    }

    public CustomerResponseDTO create(CustomerRequestDTO dto, Long senderId) {
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
        customerRepository.save(customer);

        // bắt đầu gởi thông báo
        User recipient = userRepository.findById(dto.userId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            
        // Nếu người tạo khác người nhận thì mới gởi thông báo
        if (!senderId.equals(recipient.getId())) {
            User sender = userRepository.findById(senderId).orElse(null);
            if (sender != null) {
                notificationService.createAndSendNotification(
                    sender,
                    recipient,
                    "Bạn vừa được giao khách hàng mới: " + dto.name()
                );
            }
        }
        
        return toDTO(customer);
    }

    public CustomerResponseDTO update(Long id, CustomerRequestDTO dto) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        if (!customer.getPhoneNumber().equals(dto.phoneNumber()) && customerRepository.existsByPhoneNumber(dto.phoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number already exists");
        }

        customer.setCustomerName(dto.name());
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
            c.getId(), 
            c.getCustomerName(), 
            c.getPhoneNumber(),
            c.getDateOfBirth(), 
            c.getLocation(), 
            c.isGender(), 
            c.getCreatedAt(), 
            c.getCompany().getId(),
            c.getUser().getId(), 
            c.getCustomerCode(),
            // email != null ? c.getEm : null
            c.getEmail()
        );
    }

    public String createCustomerCode() {
        return "KH" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    public String createCustomerCode(int excelRowIndex) {
        return "KH" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + excelRowIndex;
    }

    private Specification<Customer> filterByUserId(CustomerFilterRequestDTO cfDto, Long userId){
        Specification<Customer> spec = Specification.unrestricted();
        if(cfDto.search() != null && !cfDto.search().isBlank()){
            spec = spec.and(
                CustomerSpecification.hasSearch(cfDto.search())
            );
        }
        if(cfDto.gender() != null){
            spec = spec.and(
                CustomerSpecification.hasGender(
                    cfDto.gender()
                )
            );
        }
        if(cfDto.customerName() != null && !cfDto.customerName().isBlank()){
            spec = spec.and(
                CustomerSpecification.hasCustomerName(
                    cfDto.customerName()
                )
            );
        }
        if(cfDto.location() != null && ! cfDto.location().isBlank()){
            spec = spec.and(
                CustomerSpecification.hasLocation(
                    cfDto.location()
                )
            );
        }
        if(cfDto.phoneNumber() != null && !cfDto.phoneNumber().isBlank()){
            spec = spec.and(
                CustomerSpecification.hasPhoneNumber(cfDto.phoneNumber())
            );
        }
        if(cfDto.customerCode() != null && !cfDto.customerCode().isBlank()){
            spec = spec.and(
                CustomerSpecification.hasCustomerCode(cfDto.customerCode())
            );
        }
        if(cfDto.fromDateOfBirth() != null && !cfDto.toDateOfBirth().isBlank()){
            spec = spec.and(
                CustomerSpecification.dateOfBirthFrom(cfDto.fromDateOfBirth())
            );
        }
        if(cfDto.toDateOfBirth() != null && !cfDto.toDateOfBirth().isBlank()){
            spec = spec.and(
                CustomerSpecification.dateOfBirthTo(cfDto.toDateOfBirth())
            );
        }
        spec = spec.and(CustomerSpecification.hasUserId(userId));

        return spec;
    }
}
