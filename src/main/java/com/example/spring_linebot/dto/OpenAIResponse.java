package com.example.spring_linebot.dto;

import lombok.Data;

@Data
public class OpenAIResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private Choice[] choices; // Updated this line
    private Usage usage;
    private String system_fingerprint;

    @Data
    public static class Choice {
        private int index;
        private Message message;
        private Object logprobs; // You might want to define a proper type here
        private String finish_reason;
    }

    @Data
    public static class Message {
        private String role;
        private String content;
    }

    @Data
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }

}
