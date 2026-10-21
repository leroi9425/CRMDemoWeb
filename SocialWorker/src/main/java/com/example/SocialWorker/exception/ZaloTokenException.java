package com.example.SocialWorker.exception;

import lombok.RequiredArgsConstructor;

public class ZaloTokenException extends RuntimeException {
    private final int code;
    public ZaloTokenException(int code, String msg) { super(msg); this.code = code; }
    public int getCode() { return code; }
}