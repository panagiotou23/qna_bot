package com.thesis.qnabot.api.embedding.adapter.out.dto.open_ai;

import lombok.Data;

import java.util.List;

@Data
public class OpenAiEmbeddingResponseDto {
    
    private String object;
    
    private List<OpenAiEmbeddingDataDto> data;
    
}
