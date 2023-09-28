package com.thesis.qnabot.api.embedding.adapter.out.dto.pinecone;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

@Data
public class PineconeFindKNearestResponseDto {

    private JsonNode results;

    private String namespace;

    private List<PineconeMatchDto> matches;

}
