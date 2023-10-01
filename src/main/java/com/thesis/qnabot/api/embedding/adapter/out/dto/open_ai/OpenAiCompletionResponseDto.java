package com.thesis.qnabot.api.embedding.adapter.out.dto.open_ai;

import lombok.Data;

import java.util.List;

@Data
public class OpenAiCompletionResponseDto {

    private String id;

    private String object;

    private Long created;

    private String model;

    private List<OpenAiCompletionChoicesDto> choices;


}
