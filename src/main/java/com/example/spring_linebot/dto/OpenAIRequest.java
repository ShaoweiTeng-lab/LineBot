package com.example.spring_linebot.dto;

import lombok.Data;

import java.util.List;

@Data
public class OpenAIRequest {
    private String model;
    private List<Message> messages;
    private double temperature;
}
