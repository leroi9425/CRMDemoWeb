package com.crm.BackendCrm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.crm.BackendCrm.dto.Response.FacebookPageResponseDTO;
import com.crm.BackendCrm.entity.FacebookPage;
import com.crm.BackendCrm.repository.FacebookPageRepository;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.ArrayList;

@Service 
@RequiredArgsConstructor 
public class FacebookPageService {
    private final static int itemPerPage = 5;
    private final FacebookPageRepository facebookPageRepository;

    public Page<FacebookPageResponseDTO> getAllFacePageFromUserId(int index, Long userId){
        Pageable pageable = PageRequest.of(index, itemPerPage);
        Page<FacebookPage> facebookPages = facebookPageRepository.findByUserId(userId,pageable);
        return facebookPages.map(this::toDto);
    }
    public void delete(Long id, Long userId){
        if(!facebookPageRepository.existsByIdAndUserId(id, userId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thất trang facebook nào có id="+id+" của người dùng "+userId);
        facebookPageRepository.deleteById(id);
    }
    public List<FacebookPage> getAllFacebookPagesFromJson(JsonNode jsonNode) {
        System.out.println("FacebookPageService/getAllFacebookPagesFromJson");
        List<FacebookPage> facebookPages = new ArrayList<>();
        if (jsonNode.has("data")) {
            JsonNode dataNode = jsonNode.get("data");
            if (dataNode.isArray()) {
                for (JsonNode pageNode : dataNode) {
                    FacebookPage facebookPage = new FacebookPage();
                    facebookPage.setPageName(pageNode.path("name").asText());
                    facebookPage.setLongPageAccessToken(pageNode.path("access_token").asText());
                    facebookPage.setPageId(pageNode.path("id").asText());
                    
                    facebookPages.add(facebookPage);
                }
            }
        }
        System.out.println("FacebookPageService/getAllFacebookPagesFromJson - Facebook Pages: " + facebookPages);
        return facebookPages;
    }
    private FacebookPageResponseDTO toDto(FacebookPage facebookPage){
        return new FacebookPageResponseDTO(
            facebookPage.getId(),
            facebookPage.getPageId(),
            facebookPage.getPageName()
        );
    }
}
