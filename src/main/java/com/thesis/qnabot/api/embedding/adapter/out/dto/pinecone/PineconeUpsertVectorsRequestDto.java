package com.thesis.qnabot.api.embedding.adapter.out.dto.pinecone;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PineconeUpsertVectorsRequestDto {

    private List<PineconeVectorDto> vectors;

}
