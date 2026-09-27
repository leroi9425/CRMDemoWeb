package com.example.SocialService.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class PublicPageController {

    @GetMapping(value = "/privacy", produces = "text/html")
    public String privacy() {
        return """
            <html>
            <body>
                <h1>Chính sách quyền riêng tư</h1>
                <p>Đây là chính sách quyền riêng tư của ứng dụng CRM.</p>
            </body>
            </html>
            """;
    }

    @GetMapping(value = "/terms", produces = "text/html")
    public String terms() {
        return """
            <html>
            <body>
                <h1>Điều khoản dịch vụ</h1>
                <p>Đây là điều khoản dịch vụ của ứng dụng CRM.</p>
            </body>
            </html>
            """;
    }
    @GetMapping(value = "/", produces = "text/html")
    public String home() {
        return """
            <html>
            <body>
                <h1>WebCRM</h1>
                <p>Ứng dụng quản lý thông tin và chăm sóc khách hàng.</p>
            </body>
            </html>
            """;
    }
    @GetMapping("/zalo_verifierUVcE5Odl3HnLtTqpazz9A6Qhn7IAtHygCpWn.html")
    public String zaloVerfy(@RequestParam String param) {
        return new String();
    }
    
}