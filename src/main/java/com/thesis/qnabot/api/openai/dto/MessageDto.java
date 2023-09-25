package com.thesis.qnabot.api.openai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageDto {

    private String role;
    
    private String content;
    
    @Override
    public String toString() {
        return "MessageDto [role=" + role + ", content=" + content + "]";
    }
}
