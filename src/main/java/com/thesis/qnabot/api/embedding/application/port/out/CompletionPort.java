package com.thesis.qnabot.api.embedding.application.port.out;

import com.thesis.qnabot.api.embedding.domain.Query;

public interface CompletionPort {
    String getCompletion(String apiKey, Query query);
}
