package com.thesis.qnabot.api.openai.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SenderRole {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant");
    
    private final String value;
    
    private static SenderRole ofValue(String value){
        for(SenderRole role : SenderRole.values()){
            if(role.value.equals(value)){
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown sender role: " + value);
    }
    
    @Override
    public String toString(){
        return value;
    }
}
