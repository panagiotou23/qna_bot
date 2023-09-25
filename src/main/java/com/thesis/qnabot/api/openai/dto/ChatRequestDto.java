package com.thesis.qnabot.api.openai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ChatRequestDto {
    
    private Double temperature;
    
    private String user;
    
    // Completion Choices
    private int n;
    
    private boolean stream;
    
    private String model;
    
    private List<MessageDto> messages;
    
    @Override
    public String toString() {
        String fieldString = "ChatRequestDto [temperature=" + temperature + ", user=" + user + ", n=" + n + ", stream=" + stream
                + ", model=" + model;
        StringBuilder messagesString = new StringBuilder();
        for(MessageDto message : messages) {
            messagesString.append(message.toString());
        }
        return fieldString + ", messages=" + messagesString + "]";
    }

}
