package com.thesis.qnabot.api.embedding.adapter.out;

import com.google.common.collect.Lists;
import com.thesis.qnabot.api.embedding.adapter.out.dto.pinecone.*;
import com.thesis.qnabot.api.embedding.application.port.out.VectorDatabaseReadPort;
import com.thesis.qnabot.api.embedding.application.port.out.VectorDatabaseWritePort;
import com.thesis.qnabot.api.embedding.domain.Embedding;
import com.thesis.qnabot.api.embedding.domain.enums.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class VectorDatabaseAdapter implements VectorDatabaseReadPort, VectorDatabaseWritePort {

    private final RestTemplate restTemplate = new RestTemplate();

    private final int VECTORS_CHUNK_SIZE = 100;

    @Override
    public void saveEmbeddings(EmbeddingModel embeddingModel, String apiKey, List<Embedding> embeddings) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Api-Key", apiKey);
        headers.add("accept", "application/json");
        headers.add("Content-Type", "application/json");

        final var url = getUrl(embeddingModel) + "/vectors/upsert";

        final var embeddingsChunked = Lists.partition(embeddings, VECTORS_CHUNK_SIZE);
        final int[] totalEmbeddings = {0};
        embeddingsChunked.forEach(embeddingsChunk -> {
            final var body = PineconeUpsertVectorsRequestDto.builder()
                    .vectors(embeddingsChunk.stream().map(VectorDatabaseAdapterMapper.INSTANCE::fromDomain).collect(Collectors.toList()))
                    .build();

            final var response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    PineconeUpsertVectorResponseDto.class
            ).getBody();

            if (response == null || response.getUpsertedCount() != embeddingsChunk.size()) {
                throw new RuntimeException("Could not post all embeddings in Pinecone");
            }
            totalEmbeddings[0] += embeddingsChunk.size();
            log.info("Stored " + (totalEmbeddings[0]) + " of " + embeddings.size() + " total embeddings");
        });
    }

    @Override
    public List<Embedding> findKNearest(EmbeddingModel embeddingModel, String apiKey, List<Double> values, int k) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Api-Key", apiKey);
        headers.add("accept", "application/json");
        headers.add("Content-Type", "application/json");

        final var url = getUrl(embeddingModel) + "/query";

        final var body = PineconeFindKNearestRequestDto.builder()
                .vector(values)
                .topK(k)
                .build();

        final var response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                PineconeFindKNearestResponseDto.class
        ).getBody();

        if (response == null || response.getMatches() == null) {
            throw new RuntimeException("Could not get nearest embeddings in Pinecone");
        }
        return response.getMatches().stream()
                .map(VectorDatabaseAdapterMapper.INSTANCE::toDomain)
                .collect(Collectors.toList());
    }

    private String getUrl(EmbeddingModel embeddingModel) {
        String url;
        if (embeddingModel.equals(EmbeddingModel.OPEN_AI)) {
            url = "https://thesis-open-ai-test-aef333f.svc.gcp-starter.pinecone.io";
        } else if (embeddingModel.equals(EmbeddingModel.BERT)) {
            url = "https://thesis-bert-test-aef333f.svc.gcp-starter.pinecone.io";
        } else {
            throw new RuntimeException("Not a Vectorized Database Url for this embedding model");
        }
        return url;
    }

    @Mapper
    abstract static class VectorDatabaseAdapterMapper {
        private static final VectorDatabaseAdapterMapper INSTANCE =
                Mappers.getMapper(VectorDatabaseAdapterMapper.class);

        @Mapping(target = "id", source = "index")
        abstract PineconeVectorDto fromDomain(Embedding domain);

        @Mapping(target = "index", source = "id")
        abstract Embedding toDomain(PineconeMatchDto dto);
    }
}
