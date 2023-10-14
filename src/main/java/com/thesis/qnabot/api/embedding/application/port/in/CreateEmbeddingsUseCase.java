package com.thesis.qnabot.api.embedding.application.port.in;

import com.thesis.qnabot.api.embedding.domain.enums.EmbeddingModel;
import com.thesis.qnabot.api.embedding.domain.enums.VectorDatabaseModel;
import org.springframework.web.multipart.MultipartFile;

public interface CreateEmbeddingsUseCase {

    void setEmbeddingModel(EmbeddingModel model);
    void setVectorDatabaseModel(VectorDatabaseModel pinecone);
    void setEmbeddingApiKey(String key);
    void setVectorDatabaseApiKey(String key);

    void createEmbeddings(MultipartFile file);

}
