package com.thesis.qnabot.api.embedding.application.port.out;

import com.thesis.qnabot.api.embedding.domain.Embedding;
import com.thesis.qnabot.api.embedding.domain.enums.EmbeddingModel;

import java.util.List;

public interface VectorDatabaseWritePort {
    void saveEmbeddings(EmbeddingModel embeddingModel, String vectorDatabaseApiKey, List<Embedding> embeddings);
}
