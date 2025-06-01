package com.hcmute.prse_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionResponseDto {
    private boolean success;
    private String output;
    private String error;
    private long executionTime;
    private long memoryUsed;
    private String status;
    private Boolean isCorrect;
    private String actualOutput;
    private String expectedOutput;
}
