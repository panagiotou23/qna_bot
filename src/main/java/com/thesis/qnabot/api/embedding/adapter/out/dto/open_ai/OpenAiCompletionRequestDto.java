package com.thesis.qnabot.api.embedding.adapter.out.dto.open_ai;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OpenAiCompletionRequestDto {

    @Builder.Default
    private String model = "gpt-3.5-turbo";

    private List<OpenAiCompletionMessageDto> messages;

}
