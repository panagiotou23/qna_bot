package com.thesis.qnabot.api.bot_init;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BotRestController {

    private final BotService botService;

    @GetMapping("/open-ai/embedding")
    public List<Double> getEmbedding(@RequestParam String input) {
        return botService.getEmbedding(input);
    }

}
