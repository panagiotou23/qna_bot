package com.thesis.qnabot.api.embedding.application.port.out;

import com.thesis.qnabot.api.embedding.domain.enums.CompletionModel;
import com.thesis.qnabot.api.embedding.domain.Query;

public interface CompletionPort {
    String getCompletion(CompletionModel completionModel, String apiKey, Query query);
}
