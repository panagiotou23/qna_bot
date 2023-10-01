package com.thesis.qnabot.api.embedding.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Query {

    private List<String> context;

    private String message;

}
