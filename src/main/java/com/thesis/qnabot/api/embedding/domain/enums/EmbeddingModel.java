package com.thesis.qnabot.api.embedding.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmbeddingModel {

    OPEN_AI(1536, "openai"),
    BERT(768, "bert");

    private final Integer embeddingSize;
    private final String stringValue;
}
