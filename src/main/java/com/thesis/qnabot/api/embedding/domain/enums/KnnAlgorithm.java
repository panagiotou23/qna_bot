package com.thesis.qnabot.api.embedding.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KnnAlgorithm {
    COSINE("cosine"),
    EUCLIDEAN("euclidean");

    private final String stringValue;
}
