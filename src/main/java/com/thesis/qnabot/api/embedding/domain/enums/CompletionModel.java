package com.thesis.qnabot.api.embedding.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CompletionModel {
    OPEN_AI("openai"),
    ROBERTA("roberta");

    private final String stringValue;
}
