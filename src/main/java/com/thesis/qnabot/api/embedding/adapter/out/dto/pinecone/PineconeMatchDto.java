package com.thesis.qnabot.api.embedding.adapter.out.dto.pinecone;

import lombok.Data;

import java.util.List;

@Data
public class PineconeMatchDto {

    private String id;

    private Double score;

    private List<Double> values;

}
