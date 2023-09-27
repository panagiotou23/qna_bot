package com.thesis.qnabot.api.embedding.application.port.in;

import com.thesis.qnabot.api.embedding.domain.Embedding;

import java.util.List;

public interface GetEmbeddingUseCase {

    List<Embedding> getEmbeddings(String embeddingApiKey, String vectorDatabaseApiKey, String input, int chunkSize, int chunkOverlap);

}
