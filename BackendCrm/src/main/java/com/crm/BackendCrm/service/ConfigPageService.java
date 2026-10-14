package com.crm.BackendCrm.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.crm.BackendCrm.entity.Company;
import com.crm.BackendCrm.entity.FacebookPage;
import com.crm.BackendCrm.entity.User;
import com.crm.BackendCrm.repository.FacebookPageRepository;
import com.crm.BackendCrm.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor 
public class ConfigPageService {
    private final FacebookApiService facebookApiService;
    private final FacebookPageRepository facebookPageRepository;
    private final UserRepository userRepository;
    // private final FacebookUserRepository facebookUserRepository;
    public void configFacebookPage(String authorizationCode, Long userId){
        System.out.println("configFacebookPage");
        String userAccessToken = facebookApiService.getUserAccessToken(authorizationCode);
        String longUserAccessToken = facebookApiService.getLongUserAccessToken(userAccessToken);
        // luu vo db
        //đoạn làm sau
        // lay company id
        User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy user có id: "+userId));
        Company company = user.getCompany();
        System.out.println("User id: " + userId);
        System.out.println("Company id: "+company.getId());
        List<FacebookPage> facebookPages = facebookApiService.getLongPageAccessToken(longUserAccessToken);
        for (FacebookPage page : facebookPages) {
            try {
                System.out.println("ConfigPageService/configFacebookPage - Facebook Page: " + page);
                facebookApiService.doSubscribePage(page);
                // subscribe thanh cong, luu vo db
                if(!facebookPageRepository.existsByPageId(page.getPageId())){
                    page.setUser(user);
                    page.setCompany(company);

                    facebookPageRepository.save(page);
                } else {
                    FacebookPage existingPage = facebookPageRepository.findByPageId(page.getPageId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Page not found"));
                    existingPage.setPageId(page.getPageId());
                    existingPage.setPageName(page.getPageName());
                    existingPage.setLongPageAccessToken(page.getLongPageAccessToken());
                    existingPage.setUser(user);
                    existingPage.setCompany(company);

                    facebookPageRepository.save(existingPage);
                }
            } catch (Exception e) {
                System.out.println("ConfigPageService/configFacebookPage - Error subscribing to page: " + page.getPageName() + ", Error: " + e.getMessage());
            }
        }
        facebookPages.clear();
    }
}