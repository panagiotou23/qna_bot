package com.thesis.qnabot.api.embedding.application.port.in;

import com.thesis.qnabot.api.embedding.domain.Embedding;

import java.util.List;

public interface GetEmbeddingUseCase {

    List<Embedding> getEmbeddings(String apiKey, String input, int chunkSize, int chunkOverlap);

}
