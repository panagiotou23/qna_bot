package com.thesis.qnabot.api.bot_init;

import com.thesis.qnabot.api.openai.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BotService {

    private final OpenAiService openAiService;

    public List<Double> getEmbedding(String input) {
        return openAiService.getEmbedding(input);
    }
}
