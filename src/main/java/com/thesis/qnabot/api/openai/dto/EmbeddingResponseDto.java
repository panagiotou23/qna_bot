package com.thesis.qnabot.api.openai.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmbeddingResponseDto {
    
    private String object;
    
    private List<EmbeddingDataDto> data;
    
}
