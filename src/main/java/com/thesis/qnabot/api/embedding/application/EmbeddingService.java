package com.thesis.qnabot.api.embedding.application;

import com.thesis.qnabot.api.embedding.application.port.in.GetEmbeddingUseCase;
import com.thesis.qnabot.api.embedding.application.port.out.OpenAiEmbeddingReadPort;
import com.thesis.qnabot.api.embedding.domain.Embedding;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService implements GetEmbeddingUseCase {

    private final OpenAiEmbeddingReadPort openAiEmbeddingReadPort;

    @Override
    public List<Embedding> getEmbeddings(String apiKey, String document, int chunkSize, int chunkOverlap) {

        final var chucks = chuckDocument(document, chunkSize, chunkOverlap);

        return chucks.stream()
                .map(input ->
                        Embedding.builder()
                                .index(input)
                                .values(openAiEmbeddingReadPort.getEmbedding(apiKey, input))
                                .build()
                ).collect(Collectors.toList());
    }

    private List<String> chuckDocument(String input, int chunkSize, int chunkOverlap) {
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

        return chunks;
    }


}
