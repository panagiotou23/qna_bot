package com.thesis.qnabot.api.openai.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmbeddingDataDto {

    private String object;
    
    private List<Double> embedding;
    
    private Integer index;
}
