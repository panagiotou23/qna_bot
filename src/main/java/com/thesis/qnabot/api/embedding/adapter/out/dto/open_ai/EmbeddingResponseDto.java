package com.thesis.qnabot.api.embedding.adapter.out.dto.open_ai;

import lombok.Data;

import java.util.List;

@Data
public class EmbeddingResponseDto {
    
    private String object;
    
    private List<EmbeddingDataDto> data;
    
}
