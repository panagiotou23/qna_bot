package com.thesis.qnabot.api.embedding.application.port.out;

import com.thesis.qnabot.api.embedding.domain.enums.EmbeddingModel;

import java.util.List;

public interface EmbeddingReadPort {

    List<Double> getEmbedding(EmbeddingModel model, String apiKey, String input);

}
