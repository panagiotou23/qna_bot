package com.thesis.qnabot.api.embedding.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ChunkModel {
    ARBITRARY("arbitrary"),
    SENTENCES("sentences");

    private final String stringValue;
}
