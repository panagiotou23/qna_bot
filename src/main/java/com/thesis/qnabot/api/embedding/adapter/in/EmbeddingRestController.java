package com.thesis.qnabot.api.embedding.adapter.in;

import com.thesis.qnabot.api.embedding.adapter.in.dto.EmbeddingDto;
import com.thesis.qnabot.api.embedding.application.port.in.CreateEmbeddingsUseCase;
import com.thesis.qnabot.api.embedding.application.port.in.GetEmbeddingsUseCase;
import com.thesis.qnabot.api.embedding.domain.Embedding;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class EmbeddingRestController {

    private final CreateEmbeddingsUseCase createEmbeddingsUseCase;
    private final GetEmbeddingsUseCase getEmbeddingsUseCase;

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
                .map(EmbeddingRestControllerMapper.INSTANCE::fromDomain)
                .collect(Collectors.toList());
    }


    @Mapper
    abstract static class EmbeddingRestControllerMapper {
        private static final EmbeddingRestControllerMapper INSTANCE =
                Mappers.getMapper(EmbeddingRestControllerMapper.class);

        abstract EmbeddingDto fromDomain(Embedding domain);
    }
}
