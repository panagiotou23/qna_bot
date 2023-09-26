package com.thesis.qnabot.api.embedding.adapter.in;

import com.thesis.qnabot.api.embedding.adapter.in.dto.EmbeddingDto;
import com.thesis.qnabot.api.embedding.application.EmbeddingService;
import com.thesis.qnabot.api.embedding.application.port.in.GetEmbeddingUseCase;
import com.thesis.qnabot.api.embedding.domain.Embedding;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmbeddingRestController {

    private final GetEmbeddingUseCase getEmbedding;

    @GetMapping("/open-ai/embedding")
    public EmbeddingDto getEmbedding(
            @RequestParam String apiKey,
            @RequestParam String input
    ) {
        return EmbeddingRestControllerMapper.INSTANCE.fromDomain(
                getEmbedding.getEmbedding(apiKey, input)
        );
    }


    @Mapper
    abstract static class EmbeddingRestControllerMapper {
        private static final EmbeddingRestControllerMapper INSTANCE =
                Mappers.getMapper(EmbeddingRestControllerMapper.class);

        abstract EmbeddingDto fromDomain(Embedding domain);
    }
}
