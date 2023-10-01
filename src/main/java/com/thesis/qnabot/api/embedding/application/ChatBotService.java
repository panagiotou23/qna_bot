package com.thesis.qnabot.api.embedding.application;

import com.ibm.icu.text.Transliterator;
import com.thesis.qnabot.api.embedding.application.port.in.CreateEmbeddingsUseCase;
import com.thesis.qnabot.api.embedding.application.port.in.GetEmbeddingsUseCase;
import com.thesis.qnabot.api.embedding.application.port.in.QueryCompletionModelUseCase;
import com.thesis.qnabot.api.embedding.application.port.out.OpenAiCompletionPort;
import com.thesis.qnabot.api.embedding.application.port.out.OpenAiEmbeddingReadPort;
import com.thesis.qnabot.api.embedding.application.port.out.VectorDatabaseReadPort;
import com.thesis.qnabot.api.embedding.application.port.out.VectorDatabaseWritePort;
import com.thesis.qnabot.api.embedding.domain.Embedding;
import com.thesis.qnabot.api.embedding.domain.Query;
import com.thesis.qnabot.api.embedding.domain.request.QueryCompletionModelRequest;
import com.thesis.qnabot.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatBotService implements CreateEmbeddingsUseCase, GetEmbeddingsUseCase, QueryCompletionModelUseCase {

    private final OpenAiEmbeddingReadPort openAiEmbeddingReadPort;
    private final OpenAiCompletionPort openAiCompletionPort;
    private final VectorDatabaseWritePort vectorDatabaseWritePort;
    private final VectorDatabaseReadPort vectorDatabaseReadPort;

    private final int MAX_BYTES_PER_CHUNK = 511;

    @Override
    public void createEmbeddings(MultipartFile file, String embeddingApiKey, String vectorDatabaseApiKey, int chunkSize, int chunkOverlap) {

        final var document = Utils.toString(file);

        final var chucks = chunkDocument(
                new String(document.getBytes(StandardCharsets.US_ASCII), StandardCharsets.US_ASCII),
                chunkSize,
                chunkOverlap
        );

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

        input = convertStringToAscii(input);

        if (input == null || input.trim().isEmpty() || chunkSize <= 0 || chunkOverlap >= chunkSize || MAX_BYTES_PER_CHUNK <= 0) {
            log.warn("Could not get chunks out of " + input + " with chunk size " + chunkSize + ", chunk overlap " + chunkOverlap + ", and max bytes per chunk " + MAX_BYTES_PER_CHUNK);
            return chunks;
        }

        String[] words = input.split("\\s+");
        for (int i = 0; i < words.length - chunkSize + 1; i += (chunkSize - chunkOverlap)) {
            StringBuilder chunk = new StringBuilder();
            int currentByteCount = 0;

            for (int j = 0; j < chunkSize && i + j < words.length; j++) {
                String currentWord = words[i + j];
                int wordByteLength = currentWord.getBytes(StandardCharsets.US_ASCII).length;
                // If appending the current word exceeds the max bytes, break the loop
                if (currentByteCount + wordByteLength > MAX_BYTES_PER_CHUNK) {
                    break;
                }
                chunk.append(currentWord).append(" ");
                currentByteCount += wordByteLength + 1; // +1 for the space in bytes
            }

            String finalChunk = chunk.toString().trim();
            if (!finalChunk.isEmpty()) {
                chunks.add(finalChunk);
            }
        }

        if (chunks.isEmpty()) {
            // Ensure that the input does not exceed maxBytesPerChunk.
            // Truncate if necessary. Consider a more refined approach for multi-byte characters.
            String limitedInput = input.substring(0, Math.min(input.length(), MAX_BYTES_PER_CHUNK));
            chunks.add(limitedInput);
        }
        log.info("Split the document into " + chunks.size() + " chunks.");
        return chunks;
    }

    private String convertStringToAscii(String str) {
        String id = "Any-Latin; Latin-ASCII;"; // Transliteration identifier
        Transliterator trans = Transliterator.getInstance(id);
        return trans.transliterate(str);
    }

    @Override
    public String query(QueryCompletionModelRequest request) {
        final var embeddings = findKNearest(
                request.getEmbeddingApiKey(),
                request.getVectorDatabaseApiKey(),
                request.getQuery(),
                request.getK()
        );
        final var query = Query.builder()
                .context(
                        embeddings.stream()
                                .map(Embedding::getIndex)
                                .collect(Collectors.toList())
                ).message(request.getQuery())
                .build();

        return openAiCompletionPort.getCompletion(request.getCompletionApiKey(), query);
    }
}
