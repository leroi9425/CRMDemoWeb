package com.crm.BackendCrm.controller;

import com.crm.BackendCrm.dto.Request.ContactRequestDTO;
import com.crm.BackendCrm.dto.Response.ContactResponseDTO;
import com.crm.BackendCrm.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<List<ContactResponseDTO>> getAll() {
        return ResponseEntity.ok(contactService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.getById(id));
    }

    @GetMapping("/customer/{cusId}")
    public List<ContactResponseDTO> getContactByCusId(@PathVariable  Long cusId) {
        System.out.println("lấy hết contact của khách hàng");
        return contactService.getByCustomerId(cusId);
    }

    @PostMapping
    public ResponseEntity<ContactResponseDTO> create(@RequestBody ContactRequestDTO dto) {
        return ResponseEntity.ok(contactService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> update(@PathVariable Long id, @RequestBody ContactRequestDTO dto) {
        return ResponseEntity.ok(contactService.update(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contactService.delete(id);
        return ResponseEntity.ok().build();
    }
}
