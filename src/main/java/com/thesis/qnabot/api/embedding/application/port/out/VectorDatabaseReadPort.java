package com.thesis.qnabot.api.embedding.application.port.out;

import com.thesis.qnabot.api.embedding.domain.Embedding;

import java.util.List;

public interface VectorDatabaseReadPort {
    List<Embedding> findKNearest(String vectorDatabaseApiKey, List<Double> values, int k);
}
