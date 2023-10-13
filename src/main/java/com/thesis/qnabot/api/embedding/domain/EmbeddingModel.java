package com.thesis.qnabot.api.embedding.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmbeddingModel {

    OPEN_AI(1536),
    BERT(768);

    private final Integer embeddingSize;
}
