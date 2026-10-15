package com.crm.BackendCrm.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.crm.BackendCrm.entity.FacebookPage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor 
public class FacebookApiService {
    @Value("${APP_ID}")
    private static  String appId;
    @Value ("${APP_SECRET}")
    private String appSecret;

    private String redirectUri = "https://webhook.crmviet.vn/api/facebook/redirect";
    // final private String redirectUri = "https://civilisatory-overgenerously-johana.ngrok-free.dev/api/facebook/redirect";
    private final FacebookPageService facebookPageService;

    public String facebookConnect(String state){
        System.out.println("Kết nối Facebook Page ConnectWebhookController");
        System.out.println("APP_ID: " + appId);
        System.out.println("REDIRECT_URI: " + redirectUri);

        String uri = UriComponentsBuilder.fromUriString("https://www.facebook.com/v26.0/dialog/oauth")
                .queryParam("client_id", appId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "pages_show_list,pages_messaging,pages_read_engagement,pages_manage_metadata")
                .queryParam("state", state)
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

        String res = sendGetRequest(accessTokenUrl);
        JsonNode jsonResponse = stringToJsonNode(res);
        String accessToken = jsonResponse.path("access_token").asText();
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
        System.out.println("URL lấy long user access token: " + longAccessTokenUrl);

        String res = sendGetRequest(longAccessTokenUrl);
        JsonNode jsonResponse = stringToJsonNode(res);
        String longAccessToken = jsonResponse.path("access_token").asText();
        System.out.println("Long user access token: " + longAccessToken);
        
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
        System.out.println("URL lấy long page access token: ");

        String res = sendGetRequest(pageAccessTokenUrl);
        JsonNode jsonResponse = stringToJsonNode(res);

        List<FacebookPage> facebookPages = facebookPageService.getAllFacebookPagesFromJson(jsonResponse);
        for (FacebookPage page : facebookPages) {
            System.out.println("Facebook Page: " + page);
        }

        return facebookPages.isEmpty() ? null : facebookPages;
    }

    public void doSubscribePage(FacebookPage page){
        System.out.println("FacebookApiService/doSubscribePage");
        String subscribeUrl = UriComponentsBuilder
                .fromUriString("https://graph.facebook.com/v26.0/" + page.getPageId() + "/subscribed_apps")
                .queryParam("access_token", page.getLongPageAccessToken())
                .build()
                .encode()
                .toUriString();
        System.out.println("URL subscribe page: " + subscribeUrl);

        String res = sendPostRequest(
                subscribeUrl, 
                "subscribed_fields=messages,feed");
        System.out.println("Subscribe page response: " + res);
    }

    private String sendGetRequest(String url){
        RestClient client = RestClient.create();
        String res = client.get()
                .uri(url)
                .retrieve()
                .body(String.class);
        
        System.out.println("(FacebookApiService)Access token response: " + res);
        return res;
    }
    private String sendPostRequest(String url, String body){
        RestClient client = RestClient.create();
        String res = client.post()
                .uri(url)
                .body(body)
                .retrieve()
                .body(String.class);
        
        System.out.println("(FacebookApiService)Post request response: " + res);
        return res;
    }
    private JsonNode stringToJsonNode(String jsonString){
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(jsonString);
            System.out.println("JSON Node: " + jsonNode);
            return jsonNode;
        } catch (Exception e) {
            System.out.println("Error converting string to JSON Node: " + e.getMessage());
            return null;
        }
    }
}
