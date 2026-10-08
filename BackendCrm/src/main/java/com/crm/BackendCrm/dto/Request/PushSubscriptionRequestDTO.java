package com.crm.BackendCrm.dto.Request;

// cách viết này tạo ra JSON lồng nhau
public record PushSubscriptionRequestDTO(
    String endPoint,
    KeysDTO keys
) {
    public record KeysDTO(
        String p256dh,
        String auth
    ){}
}
