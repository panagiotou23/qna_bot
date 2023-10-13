package com.thesis.qnabot.api.embedding.adapter.in.dto;

import lombok.Data;

@Data
public class QueryCompletionModelRequestDto {

    private String query;

    private int k;
}
