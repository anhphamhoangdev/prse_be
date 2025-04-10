package com.hcmute.prse_be.request;

import lombok.Data;

import java.util.List;

@Data
public class QuestionRequest {
    private Long id;
    private String text;
    private List<AnswerRequest> answers;
}
