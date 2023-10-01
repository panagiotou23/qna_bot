package com.thesis.qnabot.api.embedding.application.port.in;

import com.thesis.qnabot.api.embedding.domain.request.QueryCompletionModelRequest;

public interface QueryCompletionModelUseCase {
    String query(QueryCompletionModelRequest request);
}
