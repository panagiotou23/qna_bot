package com.thesis.qnabot.api.embedding.adapter.out.dto.open_ai;

import lombok.Data;

@Data
public class OpenAiCompletionChoicesDto {

    private Long index;

    private OpenAiCompletionMessageDto message;
}
