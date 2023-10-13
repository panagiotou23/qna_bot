package com.thesis.qnabot.api.embedding.adapter.out.dto.nlp_cloud;

import lombok.Data;

@Data
public class NlpCloudRobertaCompletionResponseDto {
    private String answer;
    private Double score;
}
