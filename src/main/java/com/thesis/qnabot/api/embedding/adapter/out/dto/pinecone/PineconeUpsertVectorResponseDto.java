package com.thesis.qnabot.api.embedding.adapter.out.dto.pinecone;

import lombok.Data;

@Data
public class PineconeUpsertVectorResponseDto {
    private Integer upsertedCount;
}
