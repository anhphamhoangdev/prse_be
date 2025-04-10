package com.hcmute.prse_be.request;

import lombok.Data;

import java.util.List;

@Data
public class QuizRequest {
    private List<QuestionRequest> questions;
}
