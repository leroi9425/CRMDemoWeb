package com.example.SocialWorker.service;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Date;

public class TextChatService {
    private static final String emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}";
    private static final String phoneRegex = "(0[3|5|7|8|9]\\d{8})";
    
    public static String getEmailFromTextChat(String chatContext){
        Matcher matcherEmail = getMatcher(chatContext, emailRegex);
        if(matcherEmail.find()){
            System.out.println("recevice" + new Date());
            System.out.println("email: "+matcherEmail.group());
            System.out.println("end recevice");
            return matcherEmail.group();
        }
        return null;
    }
    public static String getPhoneNumberFromTextChat(String chatContext){
        Matcher matcherPhoneNumber = getMatcher(chatContext, phoneRegex);

        if(matcherPhoneNumber.find()){
            System.out.println("recevice" + new Date());
            System.out.println("so dien thoai: "+matcherPhoneNumber.group());
            System.out.println("end recevice");
            return matcherPhoneNumber.group();
        }
        return  null;
    }

    private static Matcher getMatcher(String text, String regex){
        Pattern pattern = Pattern.compile(regex);
        return  pattern.matcher(text);
    }
}
