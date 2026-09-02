package com.crm.BackendCrm.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Pageable;
import com.crm.BackendCrm.dto.Request.CompanyRequestDTO;
import com.crm.BackendCrm.dto.Response.CompanyResponseDTO;
import com.crm.BackendCrm.dto.Response.UserResponseDTO;
import com.crm.BackendCrm.entity.Company;
import com.crm.BackendCrm.entity.User;
import com.crm.BackendCrm.entity.Role;
import com.crm.BackendCrm.repository.CompanyRepository;
import com.crm.BackendCrm.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService{
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private int itemPerPage = 5;

    public List<CompanyResponseDTO> getAll(){
        return companyRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CompanyResponseDTO findById(Long id){
        Company company = companyRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        return toDTO(company);
    }

    public Page<CompanyResponseDTO> findAllInPage(int index){
        Pageable pageable = PageRequest.of(index, itemPerPage);
        Page<Company> companies = companyRepository.findAll(pageable);

        return companies.map(this::toDTO);
    }

    public List<UserResponseDTO> getAllUser(Long companyId){
        return userRepository.findByCompanyId(companyId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CompanyResponseDTO create(CompanyRequestDTO cDTO){
        Company company = new Company();
        company.setName(cDTO.name());
        company.setDescription(cDTO.description());
        company.setMaxUser(cDTO.maxUser());

        return toDTO(companyRepository.save(company));
    }

    public CompanyResponseDTO update(CompanyRequestDTO cDTO, Long id){
        Company company = companyRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        company.setName(cDTO.name());
        company.setDescription(cDTO.description());
        company.setMaxUser(cDTO.maxUser());

        return toDTO(companyRepository.save(company));
    }

    public void delete(Long id){
        companyRepository.deleteById(id);
    }

    private CompanyResponseDTO toDTO(Company c){
        return new CompanyResponseDTO(
            c.getId(),
            c.getName(),
            c.getDescription(),
            c.getMaxUser()
        );
    }
    private UserResponseDTO toDTO(User u){
        String roleStr = u.getRoles().stream()
        .map(Role::getName)
        .collect(Collectors.joining(","));

        return new UserResponseDTO(
            u.getId(),
            u.getUsername(),
            u.getEmail(),
            u.getFullName(),
            roleStr,
            u.getCreatedAt(),
            u.getCompany().getId()
        );
    }
}
