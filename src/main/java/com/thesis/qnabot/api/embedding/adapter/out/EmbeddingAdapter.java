package com.thesis.qnabot.api.embedding.adapter.out;

import com.thesis.qnabot.api.embedding.adapter.out.dto.nlp_cloud.NlpCloudBertEmbeddingRequestDto;
import com.thesis.qnabot.api.embedding.adapter.out.dto.nlp_cloud.NlpCloudBertEmbeddingResponseDto;
import com.thesis.qnabot.api.embedding.adapter.out.dto.open_ai.*;
import com.thesis.qnabot.api.embedding.application.port.out.EmbeddingReadPort;
import com.thesis.qnabot.api.embedding.domain.enums.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmbeddingAdapter implements EmbeddingReadPort {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String OPENAI_URL = "https://api.openai.com/v1";
    private static final String NLP_CLOUD_URL = "https://api.nlpcloud.io/v1";

    private static final String EMBEDDING_MODEL = "text-embedding-ada-002";

    @Override
    public List<Double> getEmbedding(EmbeddingModel model, final String apiKey, String input) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + apiKey);
        headers.add("Content-Type", "application/json");

        if (model.equals(EmbeddingModel.OPEN_AI)) {
            return getOpenAiEmbedding(headers, input);
        } else if (model.equals(EmbeddingModel.BERT)) {
            return getBertEmbedding(headers, input);
        } else {
            throw new RuntimeException("The Embedding Model is either not defined or not supported");
        }
    }

    private List<Double> getOpenAiEmbedding(HttpHeaders headers, String input) {
        final var url = OPENAI_URL + "/embeddings";
        OpenAiEmbeddingRequestDto body = EmbeddingAdapterMapper.INSTANCE.fromDomainToOpenAi(EMBEDDING_MODEL, input);

        OpenAiEmbeddingResponseDto response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                OpenAiEmbeddingResponseDto.class
        ).getBody();

        if (response == null) {
            throw new RuntimeException("Could not get response from OpenAI");
        }
        return response.getData().get(0).getEmbedding();
    }

    private List<Double> getBertEmbedding(HttpHeaders headers, String input) {
        try {
            Thread.sleep(400);
        } catch (Exception ignored) {
        }

        final var url = NLP_CLOUD_URL + "/paraphrase-multilingual-mpnet-base-v2/embeddings";
        final var body = NlpCloudBertEmbeddingRequestDto.builder()
                .sentences(List.of(input))
                .build();

        final var response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                NlpCloudBertEmbeddingResponseDto.class
        ).getBody();

        if (response == null) {
            throw new RuntimeException("Could not get response from OpenAI");
        }
        return response.getEmbeddings().get(0);
    }


    @Mapper
    abstract static class EmbeddingAdapterMapper {
        private static final EmbeddingAdapterMapper INSTANCE =
                Mappers.getMapper(EmbeddingAdapterMapper.class);

        abstract OpenAiEmbeddingRequestDto fromDomainToOpenAi(String model, String input);

    }
}
