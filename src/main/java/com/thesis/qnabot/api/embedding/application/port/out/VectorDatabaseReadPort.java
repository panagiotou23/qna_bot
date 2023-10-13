package com.thesis.qnabot.api.embedding.application.port.out;

import com.thesis.qnabot.api.embedding.domain.Embedding;
import com.thesis.qnabot.api.embedding.domain.EmbeddingModel;

import java.util.List;

public interface VectorDatabaseReadPort {
    List<Embedding> findKNearest(EmbeddingModel embeddingModel, String vectorDatabaseApiKey, List<Double> values, int k);
}
