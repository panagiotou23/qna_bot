package com.thesis.qnabot.api.embedding.application.port.in;

import com.thesis.qnabot.api.embedding.domain.Embedding;
import com.thesis.qnabot.api.embedding.domain.EmbeddingModel;
import com.thesis.qnabot.api.embedding.domain.VectorDatabaseModel;

import java.util.List;

public interface GetEmbeddingsUseCase {

    void setEmbeddingModel(EmbeddingModel model);
    void setVectorDatabaseModel(VectorDatabaseModel model);
    void setEmbeddingApiKey(String key);
    void setVectorDatabaseApiKey(String key);

    List<Embedding> findKNearest(String query, int k);
}
