package com.hcmute.prse_be.request;

import lombok.Data;

@Data
public class QuizSubmitRequest {
    private Long lessonId;

    private Double score;

    private Integer correctAnswers;

    private Integer totalQuestions;
}
