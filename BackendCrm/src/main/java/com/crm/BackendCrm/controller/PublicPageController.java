package com.crm.BackendCrm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}