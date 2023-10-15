package com.thesis.qnabot.api.embedding.adapter.out;

import com.google.common.collect.Lists;
import com.thesis.qnabot.api.embedding.adapter.out.dto.pinecone.*;
import com.thesis.qnabot.api.embedding.application.port.out.VectorDatabaseReadPort;
import com.thesis.qnabot.api.embedding.application.port.out.VectorDatabaseWritePort;
import com.thesis.qnabot.api.embedding.domain.Embedding;
import com.thesis.qnabot.api.embedding.domain.enums.KnnAlgorithm;
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
    private final String PINECONE_INDEX_URL = "-63159e9.svc.eu-west4-gcp.pinecone.io";
    private final String PINECONE_URL = "https://controller.eu-west4-gcp.pinecone.io";

    @Override
    public void saveEmbeddings(String apiKey, String indexName, List<Embedding> embeddings) {
        final var headers = getHeaders(apiKey);

        final var url = "https://" + indexName + PINECONE_INDEX_URL + "/vectors/upsert";

        final var embeddingsChunked = Lists.partition(embeddings, VECTORS_CHUNK_SIZE);
        final int[] totalEmbeddings = {0};
        embeddingsChunked.forEach(embeddingsChunk -> {
            log.info("Storing " + (totalEmbeddings[0] + embeddingsChunk.size()) + " of " + embeddings.size() + " total embeddings");
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
    public void deleteAllEmbeddings(String apiKey, String indexName) {
        final var headers = getHeaders(apiKey);
        final var url = PINECONE_URL + "/databases/" + indexName;
        restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                PineconeUpsertVectorResponseDto.class
        );
    }

    @Override
    public void createDatabase(String apiKey, String indexName, Integer embeddingSize, KnnAlgorithm knnAlgorithm) {
        if (knnAlgorithm == null) {
            throw new RuntimeException("KNN Algorithm is not defined");
        }

        final var headers = getHeaders(apiKey);
        final var url = PINECONE_URL + "/databases";

        final var body = PineconeCreateIndexRequestDto.builder()
                .name(indexName)
                .dimension(embeddingSize)
                .metric(knnAlgorithm.getStringValue())
                .build();
        restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                PineconeUpsertVectorResponseDto.class
        );
    }

    @Override
    public List<Embedding> findKNearest(String apiKey, String indexName, List<Double> values, int k) {

        final var headers = getHeaders(apiKey);

        final var url = "https://" + indexName + PINECONE_INDEX_URL + "/query";

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

    private HttpHeaders getHeaders(String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Api-Key", apiKey);
        headers.add("accept", "application/json");
        headers.add("Content-Type", "application/json");
        return headers;
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
