package com.example.SocialWorker.exception;

public class FacebookTokenExpiredException extends RuntimeException {
    public FacebookTokenExpiredException(String msg){
        super(msg);
    }
}
