package com.thesis.qnabot.api.embedding.application.port.in;

import com.thesis.qnabot.api.embedding.domain.enums.CompletionModel;
import com.thesis.qnabot.api.embedding.domain.enums.EmbeddingModel;
import com.thesis.qnabot.api.embedding.domain.enums.VectorDatabaseModel;
import com.thesis.qnabot.api.embedding.domain.request.QueryCompletionModelRequest;

public interface QueryCompletionModelUseCase {

    void setEmbeddingModel(EmbeddingModel model);
    void setVectorDatabaseModel(VectorDatabaseModel model);
    void setCompletionModel(CompletionModel model);
    void setEmbeddingApiKey(String key);
    void setVectorDatabaseApiKey(String key);
    void setCompletionApiKey(String key);


    String query(QueryCompletionModelRequest request);
}
