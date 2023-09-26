package com.thesis.qnabot.api.embedding.application.port.out;

import java.util.List;

public interface OpenAiEmbeddingReadPort {

    List<Double> getEmbedding(String apiKey, String input);

}
