package com.hcmute.prse_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentStatsDto {
    private long all;
    private long completed;
    private long inProgress;
    private long notStarted;
}
