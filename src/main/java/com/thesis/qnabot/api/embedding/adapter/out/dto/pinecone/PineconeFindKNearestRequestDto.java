package com.thesis.qnabot.api.embedding.adapter.out.dto.pinecone;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PineconeFindKNearestRequestDto {

    @Builder.Default
    private boolean includeValues = true;

    @Builder.Default
    private boolean includeMetadata = true;

    private List<Double> vector;

    private int topK;

}
