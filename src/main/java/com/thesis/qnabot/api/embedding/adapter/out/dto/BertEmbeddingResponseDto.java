package com.thesis.qnabot.api.embedding.adapter.out.dto;

import lombok.Data;

import java.util.List;

@Data
public class BertEmbeddingResponseDto {
    List<List<Double>> embeddings;
}
