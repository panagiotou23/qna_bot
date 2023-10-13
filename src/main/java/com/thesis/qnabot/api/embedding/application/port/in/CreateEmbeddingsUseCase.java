package com.thesis.qnabot.api.embedding.application.port.in;

import com.thesis.qnabot.api.embedding.domain.EmbeddingModel;
import org.springframework.web.multipart.MultipartFile;

public interface CreateEmbeddingsUseCase {

    void setEmbeddingModel(EmbeddingModel model);
    void setEmbeddingApiKey(String key);

    void createEmbeddings(MultipartFile file);

}
