package com.example.spring_linebot.dto;

import lombok.Data;

@Data
public class BaseResponse<T> {
    private String result ="SUCCESS" ;
    private T data;
}
