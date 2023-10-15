package com.thesis.qnabot.api.embedding.application.port.out;

import com.thesis.qnabot.api.embedding.domain.Embedding;
import com.thesis.qnabot.api.embedding.domain.enums.KnnAlgorithm;

import java.util.List;

public interface VectorDatabaseWritePort {
    void saveEmbeddings(String vectorDatabaseApiKey, String indexName, List<Embedding> embeddings);

    void deleteAllEmbeddings(String apiKey, String indexName);

    void createDatabase(String apiKey, String indexName, Integer embeddingSize, KnnAlgorithm knnAlgorithm);
}
