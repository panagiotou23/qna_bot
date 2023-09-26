package com.thesis.qnabot.api.embedding.application;

import com.thesis.qnabot.api.embedding.application.port.in.GetEmbeddingUseCase;
import com.thesis.qnabot.api.embedding.application.port.out.OpenAiEmbeddingReadPort;
import com.thesis.qnabot.api.embedding.domain.Embedding;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmbeddingService implements GetEmbeddingUseCase {

    private final OpenAiEmbeddingReadPort openAiEmbeddingReadPort;

    public Embedding getEmbedding(String apiKey, String input) {
        return Embedding.builder()
                .index(input)
                .values(openAiEmbeddingReadPort.getEmbedding(apiKey, input))
                .build();
    }
}
