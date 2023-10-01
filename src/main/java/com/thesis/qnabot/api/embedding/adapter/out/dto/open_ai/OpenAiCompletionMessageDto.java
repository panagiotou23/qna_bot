package com.thesis.qnabot.api.embedding.adapter.out.dto.open_ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiCompletionMessageDto {

    private String role;

    private String content;

}
