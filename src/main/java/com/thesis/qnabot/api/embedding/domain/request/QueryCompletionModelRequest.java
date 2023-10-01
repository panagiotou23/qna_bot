package com.thesis.qnabot.api.embedding.domain.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryCompletionModelRequest {

    private String completionApiKey;

    private String embeddingApiKey;

    private String vectorDatabaseApiKey;

    private String query;

    private int k;

}
