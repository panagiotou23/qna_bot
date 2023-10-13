package com.thesis.qnabot.api.embedding.application.port.in;

import org.springframework.web.multipart.MultipartFile;

public interface CreateEmbeddingsUseCase {

    void createEmbeddings(MultipartFile file);

}
