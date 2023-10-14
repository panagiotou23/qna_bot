package com.thesis.qnabot.api.embedding.adapter.out;

import com.thesis.qnabot.api.embedding.adapter.out.dto.nlp_cloud.NlpCloudRobertaCompletionRequestDto;
import com.thesis.qnabot.api.embedding.adapter.out.dto.nlp_cloud.NlpCloudRobertaCompletionResponseDto;
import com.thesis.qnabot.api.embedding.adapter.out.dto.open_ai.OpenAiCompletionMessageDto;
import com.thesis.qnabot.api.embedding.adapter.out.dto.open_ai.OpenAiCompletionRequestDto;
import com.thesis.qnabot.api.embedding.adapter.out.dto.open_ai.OpenAiCompletionResponseDto;
import com.thesis.qnabot.api.embedding.application.port.out.CompletionPort;
import com.thesis.qnabot.api.embedding.domain.enums.CompletionModel;
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
public class CompletionAdapter implements CompletionPort {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String OPENAI_URL = "https://api.openai.com/v1";
    private static final String NLP_CLOUD_URL = "https://api.nlpcloud.io/v1";

    @Override
    public String getCompletion(CompletionModel completionModel, String apiKey, Query query) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + apiKey);
        headers.add("Content-Type", "application/json");

        if (completionModel.equals(CompletionModel.OPEN_AI)) {
            return getOpenAiCompletion(query, headers);
        } else if (completionModel.equals(CompletionModel.ROBERTA)) {
            return getRobertaCompletion(query, headers);
        } else {
            throw new RuntimeException("The Completion Model is either not defined or not supported");
        }
    }

    private String getRobertaCompletion(Query query, HttpHeaders headers) {
        final var url = NLP_CLOUD_URL + "/roberta-base-squad2/question";
        final var body = CompletionAdapterMapper.INSTANCE.fromDomainToNlpCloud(query);

        final var response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                NlpCloudRobertaCompletionResponseDto.class
        ).getBody();

        if (response == null) {
            throw new RuntimeException("Could not get completion for query " + query.getMessage());
        }

        return response.getAnswer();
    }

    private String getOpenAiCompletion(Query query, HttpHeaders headers) {
        final var url = OPENAI_URL + "/chat/completions";
        final var body = CompletionAdapterMapper.INSTANCE.fromDomainToOpenAi(query);

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
    abstract static class CompletionAdapterMapper {
        private static final CompletionAdapterMapper INSTANCE =
                Mappers.getMapper(CompletionAdapterMapper.class);

        @Mapping(target = "messages", expression = "java( getMessagesFromQuery(domain) )")
        @Mapping(target = "model", ignore = true)
        abstract OpenAiCompletionRequestDto fromDomainToOpenAi(Query domain);

        @Mapping(target = "question", source = "message")
        @Mapping(target = "context", expression = "java(getContext(domain.getContext()))")
        abstract NlpCloudRobertaCompletionRequestDto fromDomainToNlpCloud(Query domain);

        List<OpenAiCompletionMessageDto> getMessagesFromQuery(Query query) {
            final var messages = new ArrayList<OpenAiCompletionMessageDto>();
            messages.add(
                    OpenAiCompletionMessageDto.builder()
                            .role("system")
                            .content("You are a helpful assistant. " +
                                    "Your job is to answer questions based on the context given, " +
                                    "with as short of a response as possible. " +
                                    "If you don't know the answer to the question ask you ALWAYS say 'I don't know the answer.'")
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

        String getContext(List<String> context) {
            final var builder = new StringBuilder();
            builder.append("\"You are a helpful assistant. " +
                    "Your job is to answer questions based on the context given, " +
                    "with as short of a response as possible. " +
                    "If you don't know the answer to the question ask you ALWAYS say 'I don't know the answer.'\"\n");
            context.forEach(c -> builder.append("\"").append(c).append("\"\n"));
            return builder.toString();
        }


    }
}
