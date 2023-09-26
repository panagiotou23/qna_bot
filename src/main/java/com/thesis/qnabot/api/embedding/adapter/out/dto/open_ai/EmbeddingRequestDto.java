package com.thesis.qnabot.api.embedding.adapter.out.dto.open_ai;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmbeddingRequestDto {

    private String model;
    
    private String input;
    
}
