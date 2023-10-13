package com.thesis.qnabot.api.embedding.adapter.in;

import com.thesis.qnabot.api.embedding.adapter.in.dto.EmbeddingDto;
import com.thesis.qnabot.api.embedding.adapter.in.dto.QueryCompletionModelRequestDto;
import com.thesis.qnabot.api.embedding.application.port.in.CreateEmbeddingsUseCase;
import com.thesis.qnabot.api.embedding.application.port.in.GetEmbeddingsUseCase;
import com.thesis.qnabot.api.embedding.application.port.in.QueryCompletionModelUseCase;
import com.thesis.qnabot.api.embedding.domain.CompletionModel;
import com.thesis.qnabot.api.embedding.domain.Embedding;
import com.thesis.qnabot.api.embedding.domain.EmbeddingModel;
import com.thesis.qnabot.api.embedding.domain.VectorDatabaseModel;
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
            @RequestParam("file") MultipartFile file
    ) {
        createEmbeddingsUseCase.setEmbeddingModel(EmbeddingModel.BERT);
        createEmbeddingsUseCase.setVectorDatabaseModel(VectorDatabaseModel.PINECONE);
        createEmbeddingsUseCase.createEmbeddings(
                file
        );
    }

    @GetMapping("/open-ai/embedding")
    List<EmbeddingDto> findKNearest(
            @RequestParam String query,
            @RequestParam int k
    ) {
        getEmbeddingsUseCase.setEmbeddingModel(EmbeddingModel.BERT);
        getEmbeddingsUseCase.setVectorDatabaseModel(VectorDatabaseModel.PINECONE);
        return getEmbeddingsUseCase.findKNearest(
                        query,
                        k
                ).stream()
                .map(TestRestControllerMapper.INSTANCE::fromDomain)
                .collect(Collectors.toList());
    }

    @PostMapping("open-ai/query-completion-model")
    String response(@RequestBody QueryCompletionModelRequestDto dto) {
        queryCompletionModelUseCase.setEmbeddingModel(EmbeddingModel.BERT);
        queryCompletionModelUseCase.setVectorDatabaseModel(VectorDatabaseModel.PINECONE);
        queryCompletionModelUseCase.setCompletionModel(CompletionModel.ROBERTA);
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
