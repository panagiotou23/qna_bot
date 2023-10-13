package com.thesis.qnabot.api.embedding.adapter.out.dto.nlp_cloud;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NlpCloudRobertaCompletionRequestDto {
    String question;
    String context;
}
