package com.thesis.qnabot.api.embedding.adapter.out.dto.nlp_cloud;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NlpCloudBertEmbeddingRequestDto {
    List<String> sentences;
}
