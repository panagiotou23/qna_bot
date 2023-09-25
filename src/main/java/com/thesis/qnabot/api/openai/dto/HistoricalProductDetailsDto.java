package com.thesis.qnabot.api.openai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HistoricalProductDetailsDto {

    List<HistoricalOrderEntryDto> orders;
}
