package com.thesis.qnabot.api.embedding.adapter.out.dto.nlp_cloud;

import lombok.Data;

import java.util.List;

@Data
public class NlpCloudBertEmbeddingResponseDto {
    List<List<Double>> embeddings;
}
