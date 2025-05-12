package com.hcmute.prse_be.dtos;
import lombok.Data;
import java.util.List;

@Data
public class AdminQuestionDTO {
    private Long id;
    private String text;
    private List<AdminAnswerDTO> answers;
}
