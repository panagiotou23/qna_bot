package com.thesis.qnabot.api.openai.domain;

import lombok.Data;

@Data
public class Message {
    
    private SenderRole role;
    
    private String content;
    
    @Override
    public String toString() {
        return "Message{" +
                "role=" + role +
                ", content='" + content + '\'' +
                '}';
    }
}