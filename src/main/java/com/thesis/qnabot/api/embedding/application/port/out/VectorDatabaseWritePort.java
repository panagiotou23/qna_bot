package com.thesis.qnabot.api.embedding.application.port.out;

import com.thesis.qnabot.api.embedding.domain.Embedding;
import com.thesis.qnabot.api.embedding.domain.enums.KnnAlgorithm;

import java.util.List;

public interface VectorDatabaseWritePort {
    void saveEmbeddings(String vectorDatabaseApiKey, List<Embedding> embeddings);

    void deleteAllEmbeddings(String apiKey);

    void createDatabase(String apiKey, Integer embeddingSize, KnnAlgorithm knnAlgorithm);
}
