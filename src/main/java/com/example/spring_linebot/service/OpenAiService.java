package com.example.spring_linebot.service;

import com.example.spring_linebot.dto.BaseResponse;
import com.example.spring_linebot.dto.Message;
import com.example.spring_linebot.dto.OpenAIRequest;
import com.example.spring_linebot.dto.OpenAIResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAiService {
    @Value("${openai.api.key}")
    private String openApiKey;

    private final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate = new RestTemplate();

    private ObjectMapper mapper = new ObjectMapper();

    private List<Message> msgs = new ArrayList<>(){};

    public OpenAiService(){
        msgs.add(new Message("system", "assistant回傳的文字是繁體中文。"));
    }

    public BaseResponse<String> getChatResponse(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        BaseResponse<String> rs = new BaseResponse<>();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openApiKey);
        // 構建 OpenAI API 請求
        OpenAIRequest request = new OpenAIRequest();
        request.setModel("gpt-3.5-turbo");

        if(msgs.size()>7){//防止token 量過大
            msgs.subList(1, 5).clear();//移除 五筆 (不包含 "assistant回傳的文字是繁體中文。")
        }
        msgs.add(new Message("user", prompt));//放入對話歷史紀錄
        request.setMessages(msgs);
        request.setTemperature(0.7);

        // 設置requestBody
        String requestBody = null;
        try {
            requestBody = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                OPENAI_API_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            OpenAIResponse response = null;
            // 調用 OpenAI API
            try {
                response = mapper.readValue(responseEntity.getBody(), OpenAIResponse.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            String answer = response.getChoices()[0].getMessage().getContent();
            rs.setResult("SUCCESS");
            rs.setData(answer);
            msgs.add(new Message("assistant", answer));//放入對話歷史紀錄
            // 返回回覆
            return rs;
        } else {
            rs.setResult("FAIL");
            rs.setData("Error: Unable to get response from OpenAI");
            return rs;
        }
    }
}
