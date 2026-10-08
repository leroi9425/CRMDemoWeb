package com.crm.BackendCrm.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.crm.BackendCrm.entity.FacebookPage;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

@Service 
public class FacebookApiService {
    @Value("${APP_ID}")
    private String appId;
    @Value ("${APP_SECRET}")
    private String appSecret;

    final private String redirectUri = "https://webhook.crmviet.vn/api/facebook/redirect";

    public String facebookConnect(){
        System.out.println("Kết nối Facebook Page ConnectWebhookController");
        System.out.println("APP_ID: " + appId);
        System.out.println("REDIRECT_URI: " + redirectUri);
        String uri = UriComponentsBuilder.fromUriString("https://www.facebook.com/v26.0/dialog/oauth")
                .queryParam("client_id", appId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "pages_show_list,pages_messaging,pages_read_engagement,pages_manage_metadata")
                .build()
                .encode()
                .toUriString();
        System.out.println("URL kết nối Facebook Page: " + uri);
        System.out.println("Kết thúc kết nối Facebook Page ConnectWebhookController và return");
        return uri;
    }
    public String getUserAccessToken(String authorizationCode){
        System.out.println("FacebookApiService/getUserAccessToken");
        String accessTokenUrl = UriComponentsBuilder
                .fromUriString("https://graph.facebook.com/v26.0/oauth/access_token")
                .queryParam("client_id", appId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("client_secret", appSecret)
                .queryParam("code", authorizationCode)
                .build()
                .encode()
                .toUriString();
        System.out.println("URL lấy access token: " + accessTokenUrl);

        JsonNode res = sendGetRequest(accessTokenUrl);
        String accessToken = res.path("access_token").asText();
        System.out.println("Access token: " + accessToken);

        return accessToken;
    }
    public String getLongUserAccessToken(String shortUserAccessToken){
        System.out.println("FacebookApiService/getLongUserAccessToken");
        String longAccessTokenUrl = UriComponentsBuilder
                .fromUriString("https://graph.facebook.com/v26.0/oauth/access_token")
                .queryParam("grant_type", "fb_exchange_token")
                .queryParam("client_id", appId)
                .queryParam("client_secret", appSecret)
                .queryParam("fb_exchange_token", shortUserAccessToken)
                .build()
                .encode()
                .toUriString();
        System.out.println("URL lấy long access token: " + longAccessTokenUrl);

        JsonNode res = sendGetRequest(longAccessTokenUrl);
        String longAccessToken = res.path("access_token").asText();
        System.out.println("Long access token: " + longAccessToken);
        
        return longAccessToken;
    }
    public List<FacebookPage> getLongPageAccessToken(String longUserAccessToken){
        System.out.println("FacebookApiService/getLongPageAccessToken");
        String pageAccessTokenUrl = UriComponentsBuilder
                .fromUriString("https://graph.facebook.com/v26.0/me/accounts/")
                .queryParam("access_token", longUserAccessToken)
                .build()
                .encode()
                .toUriString();
        System.out.println("URL lấy long page access token: " + pageAccessTokenUrl);

        JsonNode res = sendGetRequest(pageAccessTokenUrl);

        FacebookPageService facebookPageService = new FacebookPageService();
        List<FacebookPage> facebookPages = facebookPageService.getAllFacebookPagesFromJson(res);
        for (FacebookPage page : facebookPages) {
            System.out.println("Facebook Page: " + page);
        }

        return facebookPages.isEmpty() ? null : facebookPages;
    }

    public void doSubscribePage(FacebookPage page){
        System.out.println("FacebookApiService/doSubscribePage");
        String subscribeUrl = UriComponentsBuilder
                .fromUriString("https://graph.facebook.com/v26.0/" + page.getId() + "/subscribed_apps")
                .queryParam("access_token", page.getLongPageAccessToken())
                .build()
                .encode()
                .toUriString();
        System.out.println("URL subscribe page: " + subscribeUrl);

        JsonNode res = sendPostRequest(subscribeUrl, null);
        System.out.println("Subscribe page response: " + res);
    }

    private JsonNode sendGetRequest(String url){
        RestClient client = RestClient.create();
        JsonNode res = client.get()
                .uri(url)
                .retrieve()
                .body(JsonNode.class);
        
        System.out.println("(FacebookApiService)Access token response: " + res);
        return res;
    }
    private JsonNode sendPostRequest(String url, String body){
        RestClient client = RestClient.create();
        JsonNode res = client.post()
                .uri(url)
                .body(body)
                .retrieve()
                .body(JsonNode.class);
        
        System.out.println("(FacebookApiService)Post request response: " + res);
        return res;
    }
}
