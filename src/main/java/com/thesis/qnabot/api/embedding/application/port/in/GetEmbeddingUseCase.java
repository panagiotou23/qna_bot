package com.thesis.qnabot.api.embedding.application.port.in;

import org.springframework.web.multipart.MultipartFile;

public interface GetEmbeddingUseCase {

    void getEmbeddings(MultipartFile file, String embeddingApiKey, String vectorDatabaseApiKey, int chunkSize, int chunkOverlap);

}
