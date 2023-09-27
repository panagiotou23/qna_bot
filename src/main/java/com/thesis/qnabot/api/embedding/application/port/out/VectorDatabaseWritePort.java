package com.thesis.qnabot.api.embedding.application.port.out;

import com.thesis.qnabot.api.embedding.domain.Embedding;

import java.util.List;

public interface VectorDatabaseWritePort {
    void saveEmbeddings(String vectorDatabaseApiKey, List<Embedding> embeddings);
}
