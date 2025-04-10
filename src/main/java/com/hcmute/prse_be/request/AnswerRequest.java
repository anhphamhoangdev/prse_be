package com.hcmute.prse_be.request;

import lombok.Data;

@Data
public class AnswerRequest {
    private Long id;
    private String text;
    private Boolean isCorrect;
}
