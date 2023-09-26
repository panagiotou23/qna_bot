package com.thesis.qnabot.api.embedding.application.port.in;

import com.thesis.qnabot.api.embedding.domain.Embedding;

public interface GetEmbeddingUseCase {

    Embedding getEmbedding(String apiKey, String input);

}
