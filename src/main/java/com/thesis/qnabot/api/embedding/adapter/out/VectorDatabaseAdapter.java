package com.thesis.qnabot.api.embedding.adapter.out;

import com.thesis.qnabot.api.embedding.adapter.out.dto.pinecone.PineconeUpsertVectorResponseDto;
import com.thesis.qnabot.api.embedding.adapter.out.dto.pinecone.PineconeUpsertVectorsRequestDto;
import com.thesis.qnabot.api.embedding.adapter.out.dto.pinecone.PineconeVectorDto;
import com.thesis.qnabot.api.embedding.application.port.out.VectorDatabaseReadPort;
import com.thesis.qnabot.api.embedding.application.port.out.VectorDatabaseWritePort;
import com.thesis.qnabot.api.embedding.domain.Embedding;
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

    private final String PINECONE_DB_URL = "https://thesis-size-1536-aef333f.svc.gcp-starter.pinecone.io";

    @Override
    public void saveEmbeddings(String apiKey, List<Embedding> embeddings) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Api-Key", apiKey);
        headers.add("accept", "application/json");
        headers.add("Content-Type", "application/json");

        final var url = PINECONE_DB_URL + "/vectors/upsert";

        final var body = PineconeUpsertVectorsRequestDto.builder()
                .vectors(embeddings.stream().map(VectorDatabaseAdapterMapper.INSTANCE::fromDomain).collect(Collectors.toList()))
                .build();

        final var response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                PineconeUpsertVectorResponseDto.class
        ).getBody();

        if (response == null || response.getUpsertedCount() != embeddings.size()) {
            throw new RuntimeException("Could not post all embeddings in Pinecone");
        }
    }


    @Mapper
    abstract static class VectorDatabaseAdapterMapper {
        private static final VectorDatabaseAdapterMapper INSTANCE =
                Mappers.getMapper(VectorDatabaseAdapterMapper.class);

        @Mapping(target = "id", source = "index")
        abstract PineconeVectorDto fromDomain(Embedding domain);

    }
}
