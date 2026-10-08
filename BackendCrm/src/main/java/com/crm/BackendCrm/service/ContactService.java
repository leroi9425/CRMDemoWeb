package com.crm.BackendCrm.service;

import com.crm.BackendCrm.dto.Request.ContactRequestDTO;
import com.crm.BackendCrm.dto.Response.ContactResponseDTO;
import com.crm.BackendCrm.entity.Contact;
import com.crm.BackendCrm.entity.Customer;
import com.crm.BackendCrm.entity.Position;
import com.crm.BackendCrm.repository.ContactRepository;
import com.crm.BackendCrm.repository.CustomerRepository;
import com.crm.BackendCrm.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;
    private final CustomerRepository customerRepository;
    private final PositionRepository positionRepository;

    public List<ContactResponseDTO> getAll() {
        return contactRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ContactResponseDTO getById(Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Contact"));
        return toDTO(contact);
    }

    public List<ContactResponseDTO> getByCustomerId(Long customerId){
        Customer customer = customerRepository.findById(customerId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Customer"));
        return contactRepository.findByCustomer_CustomerCode(customer.getCustomerCode()).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ContactResponseDTO create(ContactRequestDTO dto) {
        Customer customer = customerRepository.findByCustomerCode(dto.customerCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Customer"));

        Position position = null;
        if (dto.positionId() != null) {
            position = positionRepository.findById(dto.positionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Position"));
        }

        Contact contact = new Contact();
        contact.setContactName(dto.contactName());
        contact.setPhoneNumber(dto.phoneNumber());
        contact.setEmail(dto.email());
        contact.setCustomer(customer);
        contact.setPosition(position);

        return toDTO(contactRepository.save(contact));
    }

    public ContactResponseDTO update(ContactRequestDTO dto, Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Contact"));

        Customer customer = customerRepository.findByCustomerCode(dto.customerCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Customer"));

        Position position = null;
        if (dto.positionId() != null) {
            position = positionRepository.findById(dto.positionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Position"));
        }

        contact.setContactName(dto.contactName());
        contact.setPhoneNumber(dto.phoneNumber());
        contact.setEmail(dto.email());
        contact.setCustomer(customer);
        contact.setPosition(position);

        return toDTO(contactRepository.save(contact));
    }

    public void delete(Long id) {
        if (!contactRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Contact");
        }
        contactRepository.deleteById(id);
    }

    private ContactResponseDTO toDTO(Contact c) {
        Long posId = (c.getPosition() != null) ? c.getPosition().getId() : null;

        return new ContactResponseDTO(
                c.getId(),
                c.getContactName(),
                c.getPhoneNumber(),
                c.getEmail(),
                c.getCustomer().getCustomerCode(),
                posId
        );
    }
}
