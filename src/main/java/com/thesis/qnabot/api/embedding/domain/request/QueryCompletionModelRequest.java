package com.thesis.qnabot.api.embedding.domain.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryCompletionModelRequest {

    private String query;

    private int k;

}
