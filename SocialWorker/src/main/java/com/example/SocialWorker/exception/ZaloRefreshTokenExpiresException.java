package com.example.SocialWorker.exception;

public class ZaloRefreshTokenExpiresException extends RuntimeException {
    public ZaloRefreshTokenExpiresException(String msg){
        super(msg);
    }
}
