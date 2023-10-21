package com.thesis.qnabot.api.embedding.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ChunkModel {
    SENTENCES("sentences"),
    ARBITRARY("arbitrary");

    private final String stringValue;
}
