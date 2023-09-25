package com.thesis.qnabot.api.bot_init;

import com.thesis.qnabot.api.openai.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotService {
    private final OpenAiService openAiService;
}
