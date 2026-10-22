package com.example.SocialWorker.exception;

public class FacebookPermissionException extends RuntimeException {
    // exception này ném ra khi token sai, ko có quyền
    public FacebookPermissionException(String msg){
        super(msg);
    }
}
