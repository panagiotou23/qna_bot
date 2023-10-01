package com.thesis.qnabot.api.embedding.adapter.out.dto.open_ai;

import lombok.Data;

import java.util.List;

@Data
public class OpenAiEmbeddingDataDto {

    private String object;
    
    private List<Double> embedding;
    
    private Integer index;
}
