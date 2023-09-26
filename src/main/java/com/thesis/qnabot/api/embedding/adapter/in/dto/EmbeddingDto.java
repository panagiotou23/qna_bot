package com.thesis.qnabot.api.embedding.adapter.in.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmbeddingDto {

    private String index;

    private List<Double> values;

}
