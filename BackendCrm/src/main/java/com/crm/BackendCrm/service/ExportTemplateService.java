package com.crm.BackendCrm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.crm.BackendCrm.dto.Request.ExportTemplateRequestDTO;
import com.crm.BackendCrm.dto.Response.ExportTemplateResponseDTO;
import com.crm.BackendCrm.entity.ExportTemplate;
import com.crm.BackendCrm.repository.ExportTemplateRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor 
public class ExportTemplateService {
    ExportTemplateRepository exportTemplateRepository;

    public List<ExportTemplateResponseDTO> getAllByUserId(Long id){
        System.out.print(exportTemplateRepository.findByUserId(id).stream().map(this::toDTO).collect(Collectors.toList()));
        return exportTemplateRepository.findByUserId(id).stream().map(this::toDTO).collect(Collectors.toList());
    }
    public ExportTemplateResponseDTO create(ExportTemplateRequestDTO expt, Long userId){
        ExportTemplate exportTemplate = new ExportTemplate();
        exportTemplate.setName(expt.name());
        exportTemplate.setFields(expt.fields());
        exportTemplate.setUserId(userId);
        return toDTO(exportTemplateRepository.save(exportTemplate));
    }
    public void delete(Long id){
        exportTemplateRepository.deleteById(id);
    }

    private ExportTemplateResponseDTO toDTO(ExportTemplate expt){
        return new ExportTemplateResponseDTO(
            expt.getId(),
            expt.getName(),
            expt.getFields(),
            expt.getUserId()
        );
    }
}
