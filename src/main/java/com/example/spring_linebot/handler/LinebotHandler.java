package com.example.spring_linebot.handler;

import com.example.spring_linebot.service.OpenAiService;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@LineMessageHandler
public class LinebotHandler {
    @Autowired
    private OpenAiService openAiService;

    private final Logger log = LoggerFactory.getLogger(LinebotHandler.class);

    @EventMapping
    public Message handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
        String redisIp = System.getenv("DB_REDIS_IP");
        System.out.println("Redis IP: " + redisIp);
        System.out.println("收到訊息囉 :" + event);
        log.info("event: " + event);
        final String originalMessageText = event.getMessage().getText();
        Source source = event.getSource();
        String chatRs = "";
        if (source instanceof GroupSource && originalMessageText.toLowerCase().startsWith("gpt,"))
            chatRs = openAiService.getChatResponse("Group:" + ((GroupSource) source).getGroupId(), originalMessageText).getData();
        else if (source instanceof UserSource)
            chatRs = openAiService.getChatResponse("User:" + source.getUserId(), originalMessageText).getData();
        return chatRs.isEmpty() ? null : new TextMessage(chatRs);
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }
}
