package com.thesis.qnabot.api.embedding.adapter.in.dto;

import lombok.Data;

@Data
public class QueryCompletionModelRequestDto {

    private String completionApiKey;

    private String embeddingApiKey;

    private String vectorDatabaseApiKey;

    private String query;

    private int k;
}
