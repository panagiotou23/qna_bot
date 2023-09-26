package com.thesis.qnabot.api.embedding.adapter.out;

import com.thesis.qnabot.api.embedding.adapter.out.dto.open_ai.EmbeddingRequestDto;
import com.thesis.qnabot.api.embedding.adapter.out.dto.open_ai.EmbeddingResponseDto;
import com.thesis.qnabot.api.embedding.application.port.out.OpenAiEmbeddingReadPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenAiEmbeddingAdapter implements OpenAiEmbeddingReadPort {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String OPENAI_URL = "https://api.openai.com/v1";

    private static final String EMBEDDING_MODEL = "text-embedding-ada-002";

    @Override
    public List<Double> getEmbedding(final String apiKey, String input) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + apiKey);
        headers.add("Content-Type", "application/json");

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(OPENAI_URL + "/embeddings");
        EmbeddingRequestDto request = new EmbeddingRequestDto(EMBEDDING_MODEL, input);

        EmbeddingResponseDto response = restTemplate.exchange(builder.buildAndExpand().toUri(),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                EmbeddingResponseDto.class).getBody();

        if (response == null) {
            throw new RuntimeException("Could not get response from OpenAI");
        }
        return response.getData().get(0).getEmbedding();

    }

}
