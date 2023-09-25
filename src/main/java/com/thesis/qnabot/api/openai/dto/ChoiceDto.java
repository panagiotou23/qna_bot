package com.thesis.qnabot.api.openai.dto;

import lombok.Data;

@Data
public class ChoiceDto {
    
    private Integer index;
    
    private MessageDto message;
    
    private String finish_reason;
}
