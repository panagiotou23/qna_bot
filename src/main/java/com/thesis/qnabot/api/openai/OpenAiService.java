package com.thesis.qnabot.api.openai;


import com.thesis.qnabot.api.openai.domain.Message;

import java.util.List;

public interface OpenAiService {
    
    List<Double> getEmbedding(final String input);
    
    String getCompletion(List<Message> messages, String user);
}
