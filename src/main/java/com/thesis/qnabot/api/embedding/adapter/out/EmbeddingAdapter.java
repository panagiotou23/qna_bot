package com.thesis.qnabot.api.embedding.adapter.out;

import com.thesis.qnabot.api.embedding.adapter.out.dto.BertEmbeddingRequestDto;
import com.thesis.qnabot.api.embedding.adapter.out.dto.BertEmbeddingResponseDto;
import com.thesis.qnabot.api.embedding.adapter.out.dto.open_ai.*;
import com.thesis.qnabot.api.embedding.application.port.out.CompletionPort;
import com.thesis.qnabot.api.embedding.application.port.out.EmbeddingReadPort;
import com.thesis.qnabot.api.embedding.domain.EmbeddingModel;
import com.thesis.qnabot.api.embedding.domain.Query;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmbeddingAdapter implements EmbeddingReadPort, CompletionPort {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String OPENAI_URL = "https://api.openai.com/v1";
    private static final String BERT_URL = "https://api.nlpcloud.io/v1/paraphrase-multilingual-mpnet-base-v2";

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
        OpenAiEmbeddingRequestDto body = OpenAiAdapterMapper.INSTANCE.fromDomain(EMBEDDING_MODEL, input);

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

        final var url = BERT_URL + "/embeddings";
        final var body = BertEmbeddingRequestDto.builder()
                .sentences(List.of(input))
                .build();

        final var response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                BertEmbeddingResponseDto.class
        ).getBody();

        if (response == null) {
            throw new RuntimeException("Could not get response from OpenAI");
        }
        return response.getEmbeddings().get(0);
    }

    @Override
    public String getCompletion(String apiKey, Query query) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + apiKey);
        headers.add("Content-Type", "application/json");

        final var url = OPENAI_URL + "/chat/completions";
        final var body = OpenAiAdapterMapper.INSTANCE.fromDomain(query);

        final var response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                OpenAiCompletionResponseDto.class
        ).getBody();

        if (response == null) {
            throw new RuntimeException("Could not get completion for query " + query.getMessage());
        }

        return response.getChoices().stream()
                .filter(c -> c.getIndex().equals(0L))
                .map(c -> c.getMessage().getContent())
                .findFirst()
                .orElseThrow();
    }


    @Mapper
    abstract static class OpenAiAdapterMapper {
        private static final OpenAiAdapterMapper INSTANCE =
                Mappers.getMapper(OpenAiAdapterMapper.class);

        abstract OpenAiEmbeddingRequestDto fromDomain(String model, String input);

        @Mapping(target = "messages", expression = "java( getMessagesFromQuery(domain) )")
        @Mapping(target = "model", ignore = true)
        abstract OpenAiCompletionRequestDto fromDomain(Query domain);

        List<OpenAiCompletionMessageDto> getMessagesFromQuery(Query query) {
            final var messages = new ArrayList<OpenAiCompletionMessageDto>();
            messages.add(
                    OpenAiCompletionMessageDto.builder()
                            .role("system")
                            .content("You are a helpful assistant.")
                            .build()
                    );
            messages.addAll(
                    query.getContext().stream()
                            .map(s ->
                                    OpenAiCompletionMessageDto.builder()
                                            .role("system")
                                            .content(s)
                                            .build()
                            ).collect(Collectors.toSet())
            );

            messages.add(
                    OpenAiCompletionMessageDto.builder()
                            .role("user")
                            .content(query.getMessage())
                            .build()
            );
//            ,
//            OpenAiCompletionMessageDto.builder()
//                    .role("system")
//                    .content("If any of the below information seems irrelevant ignore them.")
//                    .build()
            return messages;
        }
    }
}
