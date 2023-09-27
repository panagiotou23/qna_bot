package com.thesis.qnabot.api.embedding.adapter.out.dto.pinecone;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PineconeUpsertVectorResponseDto {
    private Integer upsertedCount;
}
