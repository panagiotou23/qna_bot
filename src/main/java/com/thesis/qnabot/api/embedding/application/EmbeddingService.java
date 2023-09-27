package com.thesis.qnabot.api.embedding.application;

import com.thesis.qnabot.api.embedding.application.port.in.GetEmbeddingUseCase;
import com.thesis.qnabot.api.embedding.application.port.out.OpenAiEmbeddingReadPort;
import com.thesis.qnabot.api.embedding.application.port.out.VectorDatabaseWritePort;
import com.thesis.qnabot.api.embedding.domain.Embedding;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService implements GetEmbeddingUseCase {

    private final OpenAiEmbeddingReadPort openAiEmbeddingReadPort;
    private final VectorDatabaseWritePort vectorDatabaseWritePort;

    @Override
    public List<Embedding> getEmbeddings(String embeddingApiKey, String vectorDatabaseApiKey, String document, int chunkSize, int chunkOverlap) {

        final var chucks = chunkDocument(document, chunkSize, chunkOverlap);

        final var embeddings = chucks.stream()
                .map(input ->
                        Embedding.builder()
                                .index(input)
                                .values(openAiEmbeddingReadPort.getEmbedding(embeddingApiKey, input))
                                .build()
                ).collect(Collectors.toList());

        vectorDatabaseWritePort.saveEmbeddings(vectorDatabaseApiKey, embeddings);

        return embeddings;
    }

    private List<String> chunkDocument(String input, int chunkSize, int chunkOverlap) {
        List<String> chunks = new ArrayList<>();

        if (input == null || input.trim().isEmpty() || chunkSize <= 0 || chunkOverlap >= chunkSize) {
            return chunks;
        }

        String[] words = input.split("\\s+");
        for (int i = 0; i < words.length - chunkSize + 1; i += (chunkSize - chunkOverlap)) {
            StringBuilder chunk = new StringBuilder();
            for (int j = 0; j < chunkSize && i + j < words.length; j++) {
                chunk.append(words[i + j]).append(" ");
            }
            chunks.add(chunk.toString().trim());
        }

        if (chunks.isEmpty()) {
            chunks.add(input);
        }
        return chunks;
    }


}
