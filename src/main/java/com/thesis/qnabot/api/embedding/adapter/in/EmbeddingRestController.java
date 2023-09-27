package com.thesis.qnabot.api.embedding.adapter.in;

import com.thesis.qnabot.api.embedding.adapter.in.dto.EmbeddingDto;
import com.thesis.qnabot.api.embedding.application.EmbeddingService;
import com.thesis.qnabot.api.embedding.application.port.in.GetEmbeddingUseCase;
import com.thesis.qnabot.api.embedding.application.port.in.PdfToStringUseCase;
import com.thesis.qnabot.api.embedding.domain.Embedding;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class EmbeddingRestController {

    private final GetEmbeddingUseCase getEmbedding;
    private final PdfToStringUseCase pdfToStringUseCase;

    @PostMapping("/pdf-to-text")
    public String getTextFromPdf(@RequestParam("file") MultipartFile file) {
        return pdfToStringUseCase.toString(file);
    }

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
