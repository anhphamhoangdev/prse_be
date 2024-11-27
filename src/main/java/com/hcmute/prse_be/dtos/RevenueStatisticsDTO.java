package com.hcmute.prse_be.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RevenueStatisticsDTO {
    private String month;
    private Double revenue;
}

