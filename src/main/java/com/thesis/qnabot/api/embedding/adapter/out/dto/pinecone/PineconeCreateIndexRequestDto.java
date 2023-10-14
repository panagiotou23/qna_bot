package com.thesis.qnabot.api.embedding.adapter.out.dto.pinecone;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PineconeCreateIndexRequestDto {
    @Builder.Default
    private String name = "thesis";
    private Integer dimension;
    private String metric;
}
