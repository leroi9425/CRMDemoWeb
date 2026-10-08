package com.crm.BackendCrm.service;

import org.springframework.stereotype.Service;

import com.crm.BackendCrm.entity.FacebookPage;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.ArrayList;

@Service 
public class FacebookPageService {
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
}
