# Spring Boot Microservice Setup Rule

Khi tư vấn hoặc hướng dẫn setup Spring Boot Microservice chạy ngầm (như Worker/Consumer):
- Nếu yêu cầu người dùng xóa thư viện `spring-boot-starter-web` (để tối ưu, tránh mở cổng Tomcat).
- Thì **BẮT BUỘC** phải nhắc họ cài lại thư viện lõi `spring-web`.

**Mục đích:** Để giữ lại các class như `RestTemplate`, `RestClient`, `HttpHeaders`... phục vụ cho việc gọi HTTP API sang các service khác. Không có thư viện này, project sẽ bị lỗi thiếu class khi gọi API.
