package com.example.spring_linebot.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LineCallBackController {

    @PostMapping("/LineBotCallBack")
    public String success(@RequestBody String message){
        System.out.println(message);
        return  message;
    }
}
