package com.thesis.qnabot.api.openai;

import com.thesis.qnabot.api.openai.domain.*;
import com.thesis.qnabot.api.openai.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Retryable(maxAttempts = 5, value = RuntimeException.class,
           backoff = @Backoff(delay = 15000, multiplier = 2))
public class OpenAiServiceImpl implements OpenAiService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    private static final String OPENAI_URL = "https://api.openai.com/v1";
    
    private static final String TOKEN = "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";
    
    private static final String EMBEDDING_MODEL = "text-embedding-ada-002";
    
    private static final String COMPLETION_MODEL = "gpt-3.5-turbo-16k";
    
    @Override
    public List<Double> getEmbedding(final String input) {
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + TOKEN);
        headers.add("Content-Type", "application/json");
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(OPENAI_URL + "/embeddings");
        EmbeddingRequestDto request = new EmbeddingRequestDto(EMBEDDING_MODEL, input);
        
        
        EmbeddingResponseDto response = restTemplate.exchange(builder.buildAndExpand().toUri(),
                                                              HttpMethod.POST,
                                                              new HttpEntity<>(request, headers),
                                                              EmbeddingResponseDto.class).getBody();
        try {
            return response.getData().get(0).getEmbedding();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    @Override
    public String getCompletion(final List<Message> messages, final String user) {
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + TOKEN);
        headers.add("Content-Type", "application/json");
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(OPENAI_URL + "/chat/completions");
        ChatRequestDto request = new ChatRequestDto(
            0.2,
            user,
            1,
            false,
            COMPLETION_MODEL,
            messages.stream().map(OpenAiServiceMapper.INSTANCE::toDto).collect(Collectors.toList()));
        
        ChatResponseDto response = restTemplate.exchange(builder.buildAndExpand().toUri(),
                                                         HttpMethod.POST,
                                                         new HttpEntity<>(request, headers),
                                                         ChatResponseDto.class).getBody();
        return response.getChoices().get(0).getMessage().getContent();
    }
    
    @Mapper
    abstract static class OpenAiServiceMapper {
        
        private static final OpenAiServiceMapper INSTANCE = Mappers.getMapper(OpenAiServiceMapper.class);
        
        
        @Mapping(target = "role", qualifiedByName = "roleEnumToValue")
        abstract MessageDto toDto(Message message);
        
        @Named("roleEnumToValue")
        String roleEnumToValue(SenderRole role) {
            return role.toString();
        }
        
    }
    
    
}
