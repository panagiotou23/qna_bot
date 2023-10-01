package com.thesis.qnabot.api.embedding.adapter.in;

import com.thesis.qnabot.api.embedding.adapter.in.dto.EmbeddingDto;
import com.thesis.qnabot.api.embedding.adapter.in.dto.QueryCompletionModelRequestDto;
import com.thesis.qnabot.api.embedding.application.port.in.CreateEmbeddingsUseCase;
import com.thesis.qnabot.api.embedding.application.port.in.GetEmbeddingsUseCase;
import com.thesis.qnabot.api.embedding.application.port.in.QueryCompletionModelUseCase;
import com.thesis.qnabot.api.embedding.domain.Embedding;
import com.thesis.qnabot.api.embedding.domain.request.QueryCompletionModelRequest;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class TestRestController {

    private final CreateEmbeddingsUseCase createEmbeddingsUseCase;
    private final GetEmbeddingsUseCase getEmbeddingsUseCase;
    private final QueryCompletionModelUseCase queryCompletionModelUseCase;

    @PostMapping("/open-ai/embedding")
    public void getEmbedding(
            @RequestParam("file") MultipartFile file,
            @RequestParam String embeddingApiKey,
            @RequestParam String vectorDatabaseApiKey,
            @RequestParam int chunkSize,
            @RequestParam int chunkOverlap
    ) {
        createEmbeddingsUseCase.createEmbeddings(
                file,
                embeddingApiKey,
                vectorDatabaseApiKey,
                chunkSize,
                chunkOverlap
        );
    }

    @GetMapping("/open-ai/embedding")
    List<EmbeddingDto> findKNearest(
            @RequestParam String embeddingApiKey,
            @RequestParam String vectorDatabaseApiKey,
            @RequestParam String query,
            @RequestParam int k
    ) {
        return getEmbeddingsUseCase.findKNearest(
                        embeddingApiKey,
                        vectorDatabaseApiKey,
                        query,
                        k
                ).stream()
                .map(TestRestControllerMapper.INSTANCE::fromDomain)
                .collect(Collectors.toList());
    }

    @PostMapping("open-ai/query-completion-model")
    String response(@RequestBody QueryCompletionModelRequestDto dto) {
        return queryCompletionModelUseCase.query(
                TestRestControllerMapper.INSTANCE.toDomain(dto)
        );
    }

    @Mapper
    abstract static class TestRestControllerMapper {
        private static final TestRestControllerMapper INSTANCE =
                Mappers.getMapper(TestRestControllerMapper.class);

        abstract EmbeddingDto fromDomain(Embedding domain);

        abstract QueryCompletionModelRequest toDomain(QueryCompletionModelRequestDto dto);
    }
}
