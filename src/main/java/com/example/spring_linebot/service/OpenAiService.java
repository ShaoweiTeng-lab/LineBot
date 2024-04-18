package com.example.spring_linebot.service;

import com.example.spring_linebot.dto.BaseResponse;
import com.example.spring_linebot.dto.Message;
import com.example.spring_linebot.dto.OpenAIRequest;
import com.example.spring_linebot.dto.OpenAIResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OpenAiService {
    @Value("${openai.api.key}")
    private String openApiKey;

    private final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate = new RestTemplate();

    private ObjectMapper mapper = new ObjectMapper();


    @Autowired
    private RedisTemplate<String, Object> msgRedisData;


    public BaseResponse<String> getChatResponse(String id, String prompt) {

        HttpHeaders headers = new HttpHeaders();
        BaseResponse<String> rs = new BaseResponse<>();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openApiKey);
        // 構建 OpenAI API 請求
        OpenAIRequest request = new OpenAIRequest();
        request.setModel("gpt-3.5-turbo");

        ListOperations opsForList = msgRedisData.opsForList();
        if (opsForList.size(id) == 0) {
            //初始化 redis
            opsForList.rightPush(id, new Message("system", "assistant回傳的文字是繁體中文。"));
        }
        // 取出 對話紀錄
        List<Message> msgs = opsForList.range(id, 0, opsForList.size(id) - 1);
        if (msgs.size() > 7) {//防止token 量過大
            msgs.subList(1, 5).clear();//移除 五筆 (不包含 "assistant回傳的文字是繁體中文。")
            removeRange(id, 1,5);
        }
        Message msg = new Message("user", prompt);
        msgs.add(msg);
        // 存回redis
        msgRedisData.opsForList().rightPush(id, msg);
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
            msgRedisData.opsForList().rightPush(id, new Message("assistant", answer));
            // 返回回覆
            return rs;
        } else {
            rs.setResult("FAIL");
            rs.setData("Error: Unable to get response from OpenAI");
            return rs;
        }
    }
    //在 Redis 中，没有直接删除指定索引範圍的命令 ,需另外指定

    public void removeRange(String key, long start, long end) {
        ListOperations<String, Object> listOps = msgRedisData.opsForList();
        for (long i = start; i <= end; i++) {
            listOps.remove(key, 0, listOps.index(key, i));
        }
    }
}
