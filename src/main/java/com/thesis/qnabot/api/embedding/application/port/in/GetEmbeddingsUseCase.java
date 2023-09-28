package com.thesis.qnabot.api.embedding.application.port.in;

import com.thesis.qnabot.api.embedding.domain.Embedding;

import java.util.List;

public interface GetEmbeddingsUseCase {
    List<Embedding> findKNearest(String embeddingApiKey, String vectorDatabaseApiKey, String query, int k);
}
