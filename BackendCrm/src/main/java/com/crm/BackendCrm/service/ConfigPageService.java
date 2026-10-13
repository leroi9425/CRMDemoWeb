package com.crm.BackendCrm.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.crm.BackendCrm.entity.FacebookPage;
import com.crm.BackendCrm.repository.FacebookPageRepository;
import com.crm.BackendCrm.repository.FacebookUserRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor 
public class ConfigPageService {
    private final FacebookApiService facebookApiService;
    private final FacebookPageRepository facebookPageRepository;
    // private final FacebookUserRepository facebookUserRepository;
    public void configFacebookPage(String authorizationCode){
        String userAccessToken = facebookApiService.getUserAccessToken(authorizationCode);
        String longUserAccessToken = facebookApiService.getLongUserAccessToken(userAccessToken);
        // luu vo db
        //đoạn làm sau
        List<FacebookPage> facebookPages = facebookApiService.getLongPageAccessToken(longUserAccessToken);
        for (FacebookPage page : facebookPages) {
            try {
                System.out.println("ConfigPageService/configFacebookPage - Facebook Page: " + page);
                facebookApiService.doSubscribePage(page);
                // subscribe thanh cong, luu vo db

                if(!facebookPageRepository.existsByPageId(page.getPageId())){
                    facebookPageRepository.save(page);
                } else {
                    FacebookPage existingPage = facebookPageRepository.findByPageId(page.getPageId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Page not found"));
                    existingPage.setPageId(page.getPageId());
                    existingPage.setPageName(page.getPageName());
                    existingPage.setLongPageAccessToken(page.getLongPageAccessToken());
                    facebookPageRepository.save(existingPage);
                }
            } catch (Exception e) {
                System.out.println("ConfigPageService/configFacebookPage - Error subscribing to page: " + page.getPageName() + ", Error: " + e.getMessage());
            }
        }
        facebookPages.clear();
    }
}