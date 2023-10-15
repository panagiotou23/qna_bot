package com.thesis.qnabot.api.embedding.application.port.in;

import com.thesis.qnabot.api.embedding.domain.enums.EmbeddingModel;
import com.thesis.qnabot.api.embedding.domain.enums.VectorDatabaseModel;
import org.springframework.web.multipart.MultipartFile;

public interface EditEmbeddingsUseCase {

    void setEmbeddingModel(EmbeddingModel model);
    void setVectorDatabaseModel(VectorDatabaseModel pinecone);
    void setEmbeddingApiKey(String key);
    void setVectorDatabaseApiKey(String key);

    void createEmbeddings(String indexName, MultipartFile file);

    void createEmbeddings(String indexName, String document);

    void deleteAllEmbeddings(String indexName);

    void createDatabase(String indexName);

}
