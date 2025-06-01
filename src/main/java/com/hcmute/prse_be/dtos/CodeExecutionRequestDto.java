package com.hcmute.prse_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionRequestDto {
    private String code;
    private String language;
    private String input;
    private String expectedOutput;
}