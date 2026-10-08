package com.crm.BackendCrm.service;

import org.springframework.stereotype.Service;
import com.crm.BackendCrm.entity.FacebookPage;
import java.util.List;

@Service
public class ConfigPageService {
    private final FacebookApiService facebookApiService;

    public ConfigPageService(FacebookApiService facebookApiService) {
        this.facebookApiService = facebookApiService;
    }

    public void configFacebookPage(String authorizationCode){
        String userAccessToken = facebookApiService.getUserAccessToken(authorizationCode);
        String longUserAccessToken = facebookApiService.getLongUserAccessToken(userAccessToken);
        List<FacebookPage> facebookPages = facebookApiService.getLongPageAccessToken(longUserAccessToken);
        for (FacebookPage page : facebookPages) {
            try {
                System.out.println("ConfigPageService/configFacebookPage - Facebook Page: " + page);
                facebookApiService.doSubscribePage(page);
                facebookPages.remove(page);
            } catch (Exception e) {
                System.out.println("ConfigPageService/configFacebookPage - Error subscribing to page: " + page.getPageName() + ", Error: " + e.getMessage());
            }
        }
    }
}