package com.thesis.qnabot.api.embedding.application;

import com.ibm.icu.text.Transliterator;
import com.thesis.qnabot.api.embedding.application.port.in.EditEmbeddingsUseCase;
import com.thesis.qnabot.api.embedding.application.port.in.QueryCompletionModelUseCase;
import com.thesis.qnabot.api.embedding.application.port.out.CompletionPort;
import com.thesis.qnabot.api.embedding.application.port.out.EmbeddingReadPort;
import com.thesis.qnabot.api.embedding.application.port.out.VectorDatabaseReadPort;
import com.thesis.qnabot.api.embedding.application.port.out.VectorDatabaseWritePort;
import com.thesis.qnabot.api.embedding.domain.*;
import com.thesis.qnabot.api.embedding.domain.enums.*;
import com.thesis.qnabot.api.embedding.domain.request.QueryCompletionModelRequest;
import com.thesis.qnabot.util.Utils;
import lombok.Getter;
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
@Getter
@Slf4j
public class ChatBotService implements EditEmbeddingsUseCase, QueryCompletionModelUseCase {

    private final EmbeddingReadPort embeddingReadPort;
    private final CompletionPort completionPort;
    private final VectorDatabaseWritePort vectorDatabaseWritePort;
    private final VectorDatabaseReadPort vectorDatabaseReadPort;


    private int chunkSize = 100;
    private int chunkOverlap = 25;

    private EmbeddingModel embeddingModel;
    private CompletionModel completionModel;
    private VectorDatabaseModel vectorDatabaseModel;
    private ChunkModel chunkModel;
    private KnnAlgorithm knnAlgorithm;

    private String embeddingApiKey;
    private String vectorDatabaseApiKey;
    private String completionApiKey;

    @Override
    public void createEmbeddings(MultipartFile file) {

        final var document = Utils.toString(file);

        createEmbeddings(document);

    }

    @Override
    public void createEmbeddings(String document) {

        final var chucks = chunkDocument(
                new String(document.getBytes(StandardCharsets.US_ASCII), StandardCharsets.US_ASCII)
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
            vectorDatabaseWritePort.saveEmbeddings(vectorDatabaseApiKey, embeddings);
        } else {
            throw new RuntimeException("The Vectorized Database Model is either not defined or not supported");
        }

    }


    @Override
    public void deleteAllEmbeddings() {
        vectorDatabaseWritePort.deleteAllEmbeddings(vectorDatabaseApiKey);
    }

    @Override
    public void createDatabase() {
        vectorDatabaseWritePort.createDatabase(vectorDatabaseApiKey, embeddingModel.getEmbeddingSize(), knnAlgorithm);
    }


    @Override
    public String query(QueryCompletionModelRequest request) {
        List<Embedding> embeddings;
        if (embeddingModel != null) {
            embeddings = findKNearest(
                    embeddingApiKey,
                    request.getK()
            );
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

        if (completionModel != null) {
            return completionPort.getCompletion(completionModel, completionApiKey, query);
        } else {
            throw new RuntimeException("The Completion Model is either not defined or not supported");
        }

    }

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
            return vectorDatabaseReadPort.findKNearest(vectorDatabaseApiKey, queryEmbedding.getValues(), k);
        } else {
            throw new RuntimeException("The Vectorized Database Model is either not defined or not supported");
        }

    }

    private List<String> chunkDocument(String input) {
        if (chunkModel == null) {
            throw new RuntimeException("The Chunking Model is either not defined or not supported");
        }

        List<String> chunks = new ArrayList<>();

        input = convertStringToAscii(input);
        if (chunkModel.equals(ChunkModel.ARBITRARY)) {
            log.info("Chunking input with the Arbitrary Method");
            if (input == null || input.trim().isEmpty() || chunkSize <= 0 || chunkOverlap >= chunkSize) {
                log.warn("Could not get chunks out of " + input + " with chunk size " + chunkSize + " and chunk overlap " + chunkOverlap);
                return chunks;
            }

            String[] words = input.split("\\s+");
            for (int i = 0; i < words.length - chunkSize + 1; i += (chunkSize - chunkOverlap)) {
                StringBuilder chunk = new StringBuilder();

                for (int j = 0; j < chunkSize && i + j < words.length; j++) {
                    String currentWord = words[i + j];
                    chunk.append(currentWord).append(" ");
                }

                String finalChunk = chunk.toString().trim();
                if (!finalChunk.isEmpty()) {
                    chunks.add(finalChunk);
                }
            }

            if (chunks.isEmpty()) {
                chunks.add(input);
            }
            log.info("Split the document into " + chunks.size() + " chunks.");
        } else if (chunkModel.equals(ChunkModel.SENTENCES)) {
            log.info("Chunking input with the Sentence Method");

            chunks.addAll(
                    List.of(input.split("\\."))
            );

        } else {
            throw new RuntimeException("The Chunking Model is either not defined or not supported");
        }
        return chunks;
    }

    private String convertStringToAscii(String str) {
        String id = "Any-Latin; Latin-ASCII;"; // Transliteration identifier
        Transliterator trans = Transliterator.getInstance(id);
        return trans.transliterate(str);
    }

}
