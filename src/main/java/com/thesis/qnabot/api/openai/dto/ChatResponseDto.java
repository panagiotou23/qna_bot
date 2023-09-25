package com.thesis.qnabot.api.openai.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatResponseDto {
    
    private String id;
    
    private String object;
    
    private Long created;
    
    private List<ChoiceDto> choices;

}
