package com.crm.BackendCrm.controller;

import com.crm.BackendCrm.dto.Request.CustomerRequestDTO;
import com.crm.BackendCrm.dto.Response.CustomerResponseDTO;
import com.crm.BackendCrm.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAuthority('XEM_KHACH_HANG')")
    public List<CustomerResponseDTO> getAll() {
        return customerService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('XEM_KHACH_HANG')")
    public CustomerResponseDTO getById(@PathVariable Long id) {
        return customerService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('THEM_KHACH_HANG')")
    public ResponseEntity<CustomerResponseDTO> create(@Valid @RequestBody CustomerRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(dto));
    }

    // @PostMapping
    // @PreAuthorize("hasAuthority('THEM_KHACH_HANG')")
    // public CustomerResponseDTO create(@Valid @RequestBody CustomerRequestDTO dto) {
    //     return customerService.create(dto);
    // }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SUA_KHACH_HANG')")
    public CustomerResponseDTO update(@PathVariable Long id, @Valid @RequestBody CustomerRequestDTO dto) {
        return customerService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('XOA_KHACH_HANG')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ResponseEntity<String> saveFileData (@RequestParam("file") MultipartFile file) throws IOException {
        customerService.saveFileData(file.getInputStream());
        return ResponseEntity.ok("file da luu vao database");
    }
    

//     @PostMapping("/import")
//     public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) {
//         if (file.isEmpty()) return ResponseEntity.badRequest().body("File rỗng!");

//         try (InputStream is = file.getInputStream();
//             Workbook workbook = new XSSFWorkbook(is)) {

//             Sheet sheet = workbook.getSheetAt(0);
//             int lastRow = sheet.getLastRowNum();

//             // 1. Đọc header (row 0) để build map: tên cột -> index
//             Row headerRow = sheet.getRow(0);
//             if (headerRow == null) {
//                 return ResponseEntity.badRequest().body("File thiếu dòng Header!");
//             }

//             for (Cell cell : headerRow) {
//             System.out.println("Cell " + cell.getColumnIndex() + ": " + cell.getStringCellValue());
// }           
//             // Map<String, Integer> columnIndex = new HashMap<>();
//             // for (Cell cell : headerRow) {
//             //     String headerName = cell.getStringCellValue().trim();
//             //     columnIndex.put(headerName, cell.getColumnIndex());
//             // }
//             // 3. Đọc dữ liệu từ row 1 trở đi, lấy theo tên cột thay vì số cứng
//             for (int i = 1; i <= lastRow; i++) {
//                 Row row = sheet.getRow(i);
//                 if (row == null) continue;

//                 // double id = row.getCell(columnIndex.get("H? và tên")).getNumericCellValue();
//                 // String name = row.getCell(columnIndex.get("Email")).getStringCellValue();
//                 // double salary = row.getCell(columnIndex.get("S? ?i?n tho?i")).getNumericCellValue();

//                 // System.out.println("Đọc được: name=" + id + ", email=" + name + ", sdt=" + salary);
//             }

//             return ResponseEntity.ok("Đã import thành công!");

//         } catch (IOException e) {
//             return ResponseEntity.status(500).body("Lỗi cấu trúc file Excel: " + e.getMessage());
//         }
//     }
}
