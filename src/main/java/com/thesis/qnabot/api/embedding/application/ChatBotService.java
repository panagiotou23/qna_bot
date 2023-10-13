package com.thesis.qnabot.api.embedding.application;

import com.ibm.icu.text.Transliterator;
import com.thesis.qnabot.api.embedding.application.port.in.CreateEmbeddingsUseCase;
import com.thesis.qnabot.api.embedding.application.port.in.GetEmbeddingsUseCase;
import com.thesis.qnabot.api.embedding.application.port.in.QueryCompletionModelUseCase;
import com.thesis.qnabot.api.embedding.application.port.out.*;
import com.thesis.qnabot.api.embedding.domain.*;
import com.thesis.qnabot.api.embedding.domain.request.QueryCompletionModelRequest;
import com.thesis.qnabot.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Setter
@Slf4j
public class ChatBotService implements CreateEmbeddingsUseCase, GetEmbeddingsUseCase, QueryCompletionModelUseCase {

    private final EmbeddingReadPort embeddingReadPort;
    private final CompletionPort completionPort;
    private final VectorDatabaseWritePort vectorDatabaseWritePort;
    private final VectorDatabaseReadPort vectorDatabaseReadPort;


    private int chunkSize = 100;
    private int chunkOverlap = 25;

    private EmbeddingModel embeddingModel;
    private CompletionModel completionModel;
    private VectorDatabaseModel vectorDatabaseModel;

    private String embeddingApiKey;
    private String vectorDatabaseApiKey;
    private String completionApiKey;

    private final int MAX_BYTES_PER_CHUNK = 511;

    @Override
    public void createEmbeddings(MultipartFile file) {

        final var document = Utils.toString(file);

        final var chucks = chunkDocument(
                new String(document.getBytes(StandardCharsets.US_ASCII), StandardCharsets.US_ASCII),
                chunkSize,
                chunkOverlap
        );

        List<Embedding> embeddings;
        if (embeddingModel != null) {
            embeddings = chucks.stream()
                    .map(input ->
                            Embedding.builder()
                                    .index(input)
                                    .values(embeddingReadPort.getEmbedding(embeddingModel, embeddingApiKey, input))
                                    .build()
                    ).collect(Collectors.toList());
        } else {
            throw new RuntimeException("The Embedding Model is either not defined or not supported");
        }

        if (vectorDatabaseModel.equals(VectorDatabaseModel.PINECONE)) {
            vectorDatabaseWritePort.saveEmbeddings(embeddingModel, vectorDatabaseApiKey, embeddings);
        } else {
            throw new RuntimeException("The Vectorized Database Model is either not defined or not supported");
        }

    }

    @Override
    public List<Embedding> findKNearest(String query, int k) {
        Embedding queryEmbedding;
        if (embeddingModel != null) {
            queryEmbedding = Embedding.builder()
                    .index(query)
                    .values(embeddingReadPort.getEmbedding(embeddingModel, embeddingApiKey, query))
                    .build();
        } else {
            throw new RuntimeException("The Embedding Model is either not defined or not supported");
        }

        if (vectorDatabaseModel.equals(VectorDatabaseModel.PINECONE)) {
            return vectorDatabaseReadPort.findKNearest(embeddingModel, vectorDatabaseApiKey, queryEmbedding.getValues(), k);
        } else {
            throw new RuntimeException("The Vectorized Database Model is either not defined or not supported");
        }

    }

    private List<String> chunkDocument(String input, int chunkSize, int chunkOverlap) {
        List<String> chunks = new ArrayList<>();

        input = convertStringToAscii(input);

        if (input == null || input.trim().isEmpty() || chunkSize <= 0 || chunkOverlap >= chunkSize) {
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
        List<Embedding> embeddings;
        if (embeddingModel.equals(EmbeddingModel.OPEN_AI)){
            embeddings = findKNearest(
                    embeddingApiKey,
                    request.getK()
            );
//        } else if (embeddingModel.equals(EmbeddingModel.BERT)) {

        } else {
            throw new RuntimeException("The Embedding Model is either not defined or not supported");
        }

        final var query = Query.builder()
                .context(
                        embeddings.stream()
                                .map(Embedding::getIndex)
                                .collect(Collectors.toList())
                ).message(request.getQuery())
                .build();

        if (completionModel.equals(CompletionModel.OPEN_AI)) {
            return completionPort.getCompletion(completionApiKey, query);
        }  else {
            throw new RuntimeException("The Completion Model is either not defined or not supported");
        }

    }
}
