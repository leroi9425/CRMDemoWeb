package com.example.SocialService.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HashService {
    public static String hmacSHA256(String key, String data) {
    try {
        Mac mac = Mac.getInstance("HmacSHA256");

        SecretKeySpec secretKey =
                new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
                );

        mac.init(secretKey);

        byte[] hash = mac.doFinal(
            data.getBytes(StandardCharsets.UTF_8)
        );

        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);

            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }

        return hexString.toString();

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
    public static String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");   // khai báo digest với thuật toán SHA-256
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            // chuyển input thành chuỗi nhị phân theo chuẩn utf8
            // ném vào digest để xử sha-256 xử lý (sha-256 yêu cầu 1 chuỗi toàn nhị phân để tính toàn trả về chuỗi 256 bit)
            // chuyển 256 bit thành phần tử của mảng byte, mỗi byte là 8 bit -> độ dài mảng = 256/8 = 32
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) { // với từng byte b trong mảng hash                
                String hex = Integer.toHexString(0xff & b); 
                // chuyển b gồm 8 bit thành hex gồm 2 phần tử và sử dụng oxff và phép and để xử lý số âm có bit đầu = 1 vd: 1111 1111 -> ff 
                if (hex.length() == 1) hexString.append('0');
                // nếu hex chỉ có 1 phần tử tức 4 bit đầu là 0000 -> điền 0 để bù
                hexString.append(hex);
                // append vào hexString
            }
            return hexString.toString(); // ép sang string và trả về
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
