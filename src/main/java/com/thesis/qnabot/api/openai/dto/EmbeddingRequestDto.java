package com.thesis.qnabot.api.openai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmbeddingRequestDto {

    private String model;
    
    private String input;
    
}
