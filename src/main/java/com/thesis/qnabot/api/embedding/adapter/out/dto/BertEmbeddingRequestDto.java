package com.thesis.qnabot.api.embedding.adapter.out.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BertEmbeddingRequestDto {
    List<String> sentences;
}
