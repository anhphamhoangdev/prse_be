package com.hcmute.prse_be.dtos;

import lombok.Data;

@Data
public class AdminAnswerDTO {
    private Long id;
    private String text;
    private Boolean isCorrect;
}