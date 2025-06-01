package com.hcmute.prse_be.service;

import com.hcmute.prse_be.dtos.CodeExecutionRequestDto;
import com.hcmute.prse_be.dtos.CodeExecutionResponseDto;
import com.hcmute.prse_be.dtos.SupportedLanguageDto;

import java.util.List;

public interface CodeExecutionService {
    CodeExecutionResponseDto executeCode(CodeExecutionRequestDto request);
    List<SupportedLanguageDto> getSupportedLanguages();
    boolean isHealthy();
}
