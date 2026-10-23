package com.crm.BackendCrm.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.crm.BackendCrm.dto.Response.ZaloOaResponseDTO;
import com.crm.BackendCrm.entity.ZaloOaAccess;
import com.crm.BackendCrm.repository.ZaloOaAccessRepository;
import com.crm.BackendCrm.repository.ZaloOaRefreshRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class ZaloOaService {
    private final static int itemPerPage = 5;
    private final ZaloOaAccessRepository zaloOaAccessRepository;
    private final ZaloOaRefreshRepository zaloOaRefreshRepository;
    
    public Page<ZaloOaResponseDTO> getAllFacePageFromUserId(int index, Long userId){
        Pageable pageable = PageRequest.of(index, itemPerPage);
        Page<ZaloOaAccess> facebookPages = zaloOaAccessRepository.findByUserId(userId,pageable);
        return facebookPages.map(this::toDto);
    }

    public void delete(Long id, Long userId){
        if (zaloOaAccessRepository.existsByIdAndUserId(id, userId))
            zaloOaAccessRepository.deleteById(id);
        if (zaloOaRefreshRepository.existsByIdAndUserId(id, userId))
            zaloOaRefreshRepository.deleteById(id);
    }

    private ZaloOaResponseDTO toDto(ZaloOaAccess oa){
        return new ZaloOaResponseDTO(oa.getId(), oa.getOaId(), oa.getOaName());
    }
}
