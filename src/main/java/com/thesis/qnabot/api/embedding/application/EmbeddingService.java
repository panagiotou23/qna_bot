package com.thesis.qnabot.api.embedding.application;

import com.thesis.qnabot.api.embedding.application.port.in.CreateEmbeddingsUseCase;
import com.thesis.qnabot.api.embedding.application.port.in.GetEmbeddingsUseCase;
import com.thesis.qnabot.api.embedding.application.port.out.OpenAiEmbeddingReadPort;
import com.thesis.qnabot.api.embedding.application.port.out.VectorDatabaseReadPort;
import com.thesis.qnabot.api.embedding.application.port.out.VectorDatabaseWritePort;
import com.thesis.qnabot.api.embedding.domain.Embedding;
import com.thesis.qnabot.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService implements CreateEmbeddingsUseCase, GetEmbeddingsUseCase {

    private final OpenAiEmbeddingReadPort openAiEmbeddingReadPort;
    private final VectorDatabaseWritePort vectorDatabaseWritePort;
    private final VectorDatabaseReadPort vectorDatabaseReadPort;

    @Override
    public void createEmbeddings(MultipartFile file, String embeddingApiKey, String vectorDatabaseApiKey, int chunkSize, int chunkOverlap) {

        final var document = Utils.toString(file);

        final var chucks = chunkDocument(document, chunkSize, chunkOverlap);

        final var embeddings = chucks.stream()
                .map(input ->
                        Embedding.builder()
                                .index(input)
                                .values(openAiEmbeddingReadPort.getEmbedding(embeddingApiKey, input))
                                .build()
                ).collect(Collectors.toList());

        vectorDatabaseWritePort.saveEmbeddings(vectorDatabaseApiKey, embeddings);

    }

    @Override
    public List<Embedding> findKNearest(String embeddingApiKey, String vectorDatabaseApiKey, String query, int k) {
        final var queryEmbedding = Embedding.builder()
                .index(query)
                .values(openAiEmbeddingReadPort.getEmbedding(embeddingApiKey, query))
                .build();

        return vectorDatabaseReadPort.findKNearest(vectorDatabaseApiKey, queryEmbedding.getValues(), k);
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
